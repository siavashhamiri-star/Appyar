package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AccountStatus
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.UserAccount
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.PersonRepository
import com.apyar.app.domain.repository.UserAccountRepository

class CreateUserAccountUseCase(
    private val userAccountRepository: UserAccountRepository,
    private val personRepository: PersonRepository,
    private val auditRepository: AuditRepository? = null
) {
    suspend operator fun invoke(
        personId: String,
        mobileNumber: String,
        accountStatus: AccountStatus = AccountStatus.ACTIVE
    ): Result<UserAccount> {
        val trimmedMobile = mobileNumber.trim()
        if (trimmedMobile.isBlank()) {
            return Result.failure(IllegalArgumentException("شماره همراه کاربر نمی‌تواند خالی باشد."))
        }

        val person = personRepository.getPersonById(personId)
            ?: return Result.failure(IllegalArgumentException("شخص مورد نظر یافت نشد."))

        val existingAccount = userAccountRepository.getUserAccountByMobile(trimmedMobile)
        if (existingAccount != null) {
            return Result.failure(IllegalStateException("حساب کاربری با این شماره همراه قبلاً ایجاد شده است."))
        }

        val account = UserAccount(
            personId = personId,
            mobileNumber = trimmedMobile,
            accountStatus = accountStatus
        )

        val createdAccount = userAccountRepository.createUserAccount(account)

        auditRepository?.recordEvent(
            AuditEvent(
                actor = "SYSTEM",
                action = "CREATE_USER_ACCOUNT",
                entity = "UserAccount",
                entityId = createdAccount.id,
                details = "حساب کاربری با شماره ${createdAccount.mobileNumber} ایجاد شد."
            )
        )

        return Result.success(createdAccount)
    }
}

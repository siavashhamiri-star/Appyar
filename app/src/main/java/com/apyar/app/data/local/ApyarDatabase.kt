package com.apyar.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.apyar.app.data.local.dao.AgreementDao
import com.apyar.app.data.local.dao.AgreementDocumentDao
import com.apyar.app.data.local.dao.AuditEventDao
import com.apyar.app.data.local.dao.BuildingBillDao
import com.apyar.app.data.local.dao.BuildingDao
import com.apyar.app.data.local.dao.BuildingMemberDao
import com.apyar.app.data.local.dao.BuildingServiceProviderDao
import com.apyar.app.data.local.dao.ChargePeriodDao
import com.apyar.app.data.local.dao.ChargeRuleDao
import com.apyar.app.data.local.dao.CustomFormulaDao
import com.apyar.app.data.local.dao.DelegatedPermissionDao
import com.apyar.app.data.local.dao.FinancialAccountDao
import com.apyar.app.data.local.dao.FinancialTransactionDao
import com.apyar.app.data.local.dao.InvoiceDao
import com.apyar.app.data.local.dao.MaintenanceRecordDao
import com.apyar.app.data.local.dao.ParkingAssignmentDao
import com.apyar.app.data.local.dao.ParkingSpaceDao
import com.apyar.app.data.local.dao.PaymentRecordDao
import com.apyar.app.data.local.dao.PersonDao
import com.apyar.app.data.local.dao.ServiceProviderDao
import com.apyar.app.data.local.dao.ServiceRecordDao
import com.apyar.app.data.local.dao.StorageAssignmentDao
import com.apyar.app.data.local.dao.StorageUnitDao
import com.apyar.app.data.local.dao.UnitChargeDao
import com.apyar.app.data.local.dao.UnitDao
import com.apyar.app.data.local.dao.UnitPersonRelationDao
import com.apyar.app.data.local.dao.UserAccountDao
import com.apyar.app.data.local.entity.AgreementDocumentEntity
import com.apyar.app.data.local.entity.AgreementEntity
import com.apyar.app.data.local.entity.AuditEventEntity
import com.apyar.app.data.local.entity.BuildingBillEntity
import com.apyar.app.data.local.entity.BuildingEntity
import com.apyar.app.data.local.entity.BuildingMemberEntity
import com.apyar.app.data.local.entity.BuildingServiceProviderEntity
import com.apyar.app.data.local.entity.ChargePeriodEntity
import com.apyar.app.data.local.entity.ChargeRuleEntity
import com.apyar.app.data.local.entity.CustomFormulaEntity
import com.apyar.app.data.local.entity.DelegatedPermissionEntity
import com.apyar.app.data.local.entity.FinancialAccountEntity
import com.apyar.app.data.local.entity.FinancialTransactionEntity
import com.apyar.app.data.local.entity.InvoiceEntity
import com.apyar.app.data.local.entity.MaintenanceRecordEntity
import com.apyar.app.data.local.entity.ParkingAssignmentEntity
import com.apyar.app.data.local.entity.ParkingSpaceEntity
import com.apyar.app.data.local.entity.PaymentRecordEntity
import com.apyar.app.data.local.entity.PersonEntity
import com.apyar.app.data.local.entity.ServiceProviderEntity
import com.apyar.app.data.local.entity.ServiceRecordEntity
import com.apyar.app.data.local.entity.StorageAssignmentEntity
import com.apyar.app.data.local.entity.StorageUnitEntity
import com.apyar.app.data.local.entity.UnitChargeEntity
import com.apyar.app.data.local.entity.UnitEntity
import com.apyar.app.data.local.entity.UnitPersonRelationEntity
import com.apyar.app.data.local.entity.UserAccountEntity

@Database(
    entities = [
        BuildingEntity::class,
        UnitEntity::class,
        PersonEntity::class,
        UnitPersonRelationEntity::class,
        UserAccountEntity::class,
        BuildingMemberEntity::class,
        DelegatedPermissionEntity::class,
        AuditEventEntity::class,
        FinancialAccountEntity::class,
        FinancialTransactionEntity::class,
        ChargeRuleEntity::class,
        CustomFormulaEntity::class,
        ChargePeriodEntity::class,
        UnitChargeEntity::class,
        ParkingSpaceEntity::class,
        ParkingAssignmentEntity::class,
        StorageUnitEntity::class,
        StorageAssignmentEntity::class,
        AgreementEntity::class,
        AgreementDocumentEntity::class,
        ServiceProviderEntity::class,
        BuildingServiceProviderEntity::class,
        ServiceRecordEntity::class,
        MaintenanceRecordEntity::class,
        InvoiceEntity::class,
        PaymentRecordEntity::class,
        BuildingBillEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class ApyarDatabase : RoomDatabase() {
    abstract val buildingDao: BuildingDao
    abstract val unitDao: UnitDao
    abstract val personDao: PersonDao
    abstract val unitPersonRelationDao: UnitPersonRelationDao
    abstract val userAccountDao: UserAccountDao
    abstract val buildingMemberDao: BuildingMemberDao
    abstract val delegatedPermissionDao: DelegatedPermissionDao
    abstract val auditEventDao: AuditEventDao

    // Stage 3 DAOs
    abstract val financialAccountDao: FinancialAccountDao
    abstract val financialTransactionDao: FinancialTransactionDao
    abstract val chargeRuleDao: ChargeRuleDao
    abstract val customFormulaDao: CustomFormulaDao
    abstract val chargePeriodDao: ChargePeriodDao
    abstract val unitChargeDao: UnitChargeDao

    // Stage 4 DAOs
    abstract val parkingSpaceDao: ParkingSpaceDao
    abstract val parkingAssignmentDao: ParkingAssignmentDao
    abstract val storageUnitDao: StorageUnitDao
    abstract val storageAssignmentDao: StorageAssignmentDao
    abstract val agreementDao: AgreementDao
    abstract val agreementDocumentDao: AgreementDocumentDao

    // Stage 6 DAOs
    abstract val serviceProviderDao: ServiceProviderDao
    abstract val buildingServiceProviderDao: BuildingServiceProviderDao
    abstract val serviceRecordDao: ServiceRecordDao
    abstract val maintenanceRecordDao: MaintenanceRecordDao
    abstract val invoiceDao: InvoiceDao
    abstract val paymentRecordDao: PaymentRecordDao
    abstract val buildingBillDao: BuildingBillDao

    companion object {
        const val DATABASE_NAME = "apyar_db"

        @Volatile
        private var INSTANCE: ApyarDatabase? = null

        fun getInstance(context: Context): ApyarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApyarDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

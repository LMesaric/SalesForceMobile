package hr.atoscvc.salesforcemobile

import android.content.res.Resources
import java.io.Serializable

data class Company(
        var documentID: String? = null,     // 'documentID' set to 'null' means we are creating a new Company
        var status: Int = 0,                // mandatory!
        var OIB: String = "",               // mandatory!
        var name: String = "",              // mandatory!
        var webPage: String? = null,
        var cvsSegment: Int = 0,
        var details: String? = null,
        var phone: String? = null,
        var communicationType: Int = 0,
        var employees: Int = 0,
        var income: String? = null
) : Serializable {

    fun toFormattedString(resources: Resources): String {
        // Deliberately leaving out company status as it is most likely not important when using this method.
        with(StringBuilder()) {
            append(name)
            append("\r\n${resources.getString(R.string.formattingOib)}: $OIB")
            if (webPage != null) append("\r\n${resources.getString(R.string.formattingWeb)}: $webPage")
            if (phone != null) append("\r\n${resources.getString(R.string.formattingPhone)}: $phone")
            if (cvsSegment > 0) append("\r\n${resources.getString(R.string.formattingCvs)}: ${resources.getStringArray(R.array.companyCVS_array)[cvsSegment]}")
            if (communicationType > 0) append("\r\n${resources.getString(R.string.formattingCommType)}: ${resources.getStringArray(R.array.companyCommunicationType_array)[communicationType]}")
            if (income != null) append("\r\n${resources.getString(R.string.formattingIncome)}: $income")
            if (employees > 0) append("\r\n${resources.getString(R.string.formattingEmployees)}: ${resources.getStringArray(R.array.companyEmployees_array)[employees]}")
            if (details != null) append("\r\n${resources.getString(R.string.formattingDetails)}: $details")
            return this.toString()
        }
    }
}

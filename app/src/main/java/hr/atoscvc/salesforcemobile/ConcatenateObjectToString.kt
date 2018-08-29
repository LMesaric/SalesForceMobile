package hr.atoscvc.salesforcemobile

import android.content.res.Resources

object ConcatenateObjectToString {
    fun concatenateContactName(contact: Contact, resources: Resources, fullName: Boolean = true): String {
        val contactTitle: String = if (contact.title == 0) {
            ""
        } else {
            resources.getStringArray(R.array.contactTitle_array)[contact.title] + " "
        }
        return if (fullName) {
            "$contactTitle${contact.firstName} ${contact.lastName}"
        } else {
            "$contactTitle${contact.lastName}"
        }
    }

    fun concatenateCompany(company: Company, resources: Resources): String {
        // company.status is deliberately not in this list
        val result = StringBuilder()

        result.append(company.name).append(" ")
        result.append(company.OIB).append(" ")
        if (company.cvsSegment > 0) {
            result.append(resources.getStringArray(R.array.companyCVS_array)[company.cvsSegment]).append(" ")
        }
        if (company.webPage != null) {
            result.append(company.webPage).append(" ")
        }
        if (company.details != null) {
            result.append(company.details).append(" ")
        }
        if (company.phone != null) {
            result.append(company.phone).append(" ")
        }
        if (company.communicationType > 0) {
            result.append(resources.getStringArray(R.array.companyCommunicationType_array)[company.communicationType]).append(" ")
        }
        if (company.income != null) {
            result.append(company.income).append(" ")
        }
        if (company.employees > 0) {
            result.append(resources.getStringArray(R.array.companyEmployees_array)[company.employees]).append(" ")
        }

        return result.toString()
    }

    fun concatenateContact(contact: Contact, resources: Resources): String {
        // contact.status is deliberately not in this list
        val result = StringBuilder()

        result.append(concatenateContactName(contact, resources, true)).append(" ")
        result.append(contact.company?.name).append(" ")
        if (contact.details != null) {
            result.append(contact.details).append(" ")
        }
        if (contact.phone != null) {
            result.append(contact.phone).append(" ")
        }
        if (contact.email != null) {
            result.append(contact.email).append(" ")
        }
        if (contact.preferredTime > 0) {
            result.append(resources.getStringArray(R.array.contactPreferredTime_array)[contact.preferredTime]).append(" ")
        }

        return result.toString()
    }
}

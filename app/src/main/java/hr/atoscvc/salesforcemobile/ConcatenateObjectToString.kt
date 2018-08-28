package hr.atoscvc.salesforcemobile

import android.content.res.Resources

object ConcatenateObjectToString {
    fun concatenateContactName(contact: Contact, resources: Resources, formal: Boolean = true): String {
        val contactTitle: String = if (contact.title == 0) {
            ""
        } else {
            resources.getStringArray(R.array.contactTitle_array)[contact.title] + " "
        }
        return if (formal) {
            "$contactTitle${contact.firstName} ${contact.lastName}"
        } else {
            "$contactTitle${contact.lastName}"
        }
    }

    fun concatenateCompany(company: Company, resources: Resources): String {
        val result = StringBuilder("")
                .append(company.name).append(" ")
                .append(company.webPage).append(" ")
                .append(company.details).append(" ")
                .append(company.phone).append(" ")
                .append(company.OIB).append(" ")
                .append(resources.getStringArray(R.array.status_array)[company.status]).append(" ")

        if (company.cvsSegment > 0) {
            result.append(resources.getStringArray(R.array.companyCVS_array)[company.cvsSegment]).append(" ")
        }
        if (company.communicationType > 0) {
            result.append(resources.getStringArray(R.array.companyCommunicationType_array)[company.communicationType]).append(" ")
        }
        if (company.employees > 0) {
            result.append(resources.getStringArray(R.array.companyEmployees_array)[company.employees]).append(" ")
        }

        return result.toString()
    }

    fun concatenateContact(contact: Contact, resources: Resources): String {
        val result = StringBuilder("")
                .append(contact.firstName).append(" ")
                .append(contact.lastName).append(" ")
                .append(contact.details).append(" ")
                .append(contact.phone).append(" ")
                .append(contact.company?.name).append(" ")
                .append(contact.email).append(" ")
                .append(resources.getStringArray(R.array.status_array)[contact.status]).append(" ")

        if (contact.preferredTime > 0) {
            result.append(resources.getStringArray(R.array.contactPreferredTime_array)[contact.preferredTime]).append(" ")
        }
        if (contact.title > 0) {
            result.append(resources.getStringArray(R.array.contactTitle_array)[contact.title]).append(" ")
        }

        return result.toString()
    }
}

package hr.atoscvc.salesforcemobile

import android.content.res.Resources
import hr.atoscvc.salesforcemobile.ConcatenateObjectToString.concatenateCompany
import hr.atoscvc.salesforcemobile.ConcatenateObjectToString.concatenateContact

object SearchFilter {
    /**
     * @param query - keywords separated by blanks, empty query matches any data
     * @param data - data that is checked to see if it matches the query (Contact or Company)
     * @param matchAll - match all keywords or at least one
     * @param matchCase - match only keywords with the same case
     * @param onlyActive - match only active data (true), only inactive data (false), or both (null)
     * @param onlyWords - match only beginning of words and not middle (should 'bc' match 'Abc'?)
     */
    fun satisfiesQuery(
            query: String,
            data: Any,
            matchAll: Boolean,
            matchCase: Boolean,
            onlyActive: Boolean?,
            onlyWords: Boolean,
            resources: Resources
    ): Boolean {

        var dataString: String
        var isDataActive: Boolean
        try {
            dataString = concatenateContact(data as Contact, resources)
            isDataActive = data.status == 0
        } catch (e: ClassCastException) {
            dataString = concatenateCompany(data as Company, resources)
            isDataActive = data.status == 0
        }

        if (onlyActive == true && !isDataActive) {
            return false
        } else if (onlyActive == false && isDataActive) {
            return false
        } else if (query.isBlank()) {
            return true
        }

        val keywordList: List<String> = query.trim().split("\\s+".toRegex())

        var prepareRegex: String = if (matchAll) {
            if (onlyWords) {
                keywordList.joinToString(separator = "", postfix = ".+") { "(?=.*\\b$it)" }
            } else {
                keywordList.joinToString(separator = "", postfix = ".+") { "(?=.*$it)" }
            }
        } else {
            if (onlyWords) {
                keywordList.joinToString(separator = "|") { "\\b$it" }
            } else {
                keywordList.joinToString(separator = "|")
            }
        }

        if (!matchCase) {
            dataString = dataString.toLowerCase()
            prepareRegex = prepareRegex.toLowerCase()
        }

        return dataString.matches(prepareRegex.toRegex())
    }
}

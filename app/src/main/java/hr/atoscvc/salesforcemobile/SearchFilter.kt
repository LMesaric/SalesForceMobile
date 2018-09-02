package hr.atoscvc.salesforcemobile

import android.content.Context
import android.content.SharedPreferences
import hr.atoscvc.salesforcemobile.ConcatenateObjectToString.concatenateCompany
import hr.atoscvc.salesforcemobile.ConcatenateObjectToString.concatenateContact
import java.util.regex.Pattern

object SearchFilter {

    var preferencesChanged: Boolean = true      // change this to true whenever search preferences are changed by the user or in code

    private var onlyActive: Boolean? = true     // match only active data (true), only inactive data (false), or both (null)
    private var matchAll: Boolean = false       // match all keywords or at least one
    private var matchCase: Boolean = false      // match only keywords with the same case
    private var matchWords: Boolean = false     // match only beginning of words and not middle (should 'bc' match 'Abc'?)

    private fun updateLocalPreferences(context: Context) {
        if (preferencesChanged) {
            preferencesChanged = false

            val sharedPref: SharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.SHARED_PREFERENCES_SEARCH_SETTINGS),
                    Context.MODE_PRIVATE
            )

            val selectedRadioId = sharedPref.getInt(
                    context.getString(R.string.SHARED_PREFERENCES_RADIO_BUTTON_SELECTED_ID),
                    R.id.rbSettingsActive
            )
            onlyActive = when (selectedRadioId) {
                R.id.rbSettingsActive -> true
                R.id.rbSettingsInactive -> false
                R.id.rbSettingsAll -> null
                else -> true
            }

            matchAll = sharedPref.getBoolean(context.getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_ALL), false)
            matchCase = sharedPref.getBoolean(context.getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_CASE), false)
            matchWords = sharedPref.getBoolean(context.getString(R.string.SHARED_PREFERENCES_CHECK_BOX_MATCH_WORDS), false)
        }
    }

    /**
     * @param query - keywords separated by blanks - empty and null query match any data
     * @param data - data that is checked to see if it matches the query (Contact or Company object)
     * @param context - needed for string arrays in data concatenation and for fetching shared preferences
     */
    fun satisfiesQuery(
            query: String?,
            data: Any,
            context: Context
    ): Boolean {

        updateLocalPreferences(context)

        var dataString: String
        var isDataActive: Boolean
        try {
            dataString = concatenateContact(data as Contact, context.resources)
            isDataActive = data.status == 0
        } catch (e: ClassCastException) {
            dataString = concatenateCompany(data as Company, context.resources)
            isDataActive = data.status == 0
        }

        if (onlyActive == true && !isDataActive) {
            return false
        } else if (onlyActive == false && isDataActive) {
            return false
        } else if (query.isNullOrBlank()) {
            return true
        }

        val keywordList: List<String> = query!!.trim().split("\\s+".toRegex())

        var prepareRegex: String = if (matchAll) {
            if (matchWords) {
                keywordList.joinToString(separator = "", postfix = ".+") { "(?=.*\\b$it)" }
            } else {
                keywordList.joinToString(separator = "", postfix = ".+") { "(?=.*$it)" }
            }
        } else {
            if (matchWords) {
                keywordList.joinToString(separator = "|") { "\\b$it" }
            } else {
                keywordList.joinToString(separator = "|")
            }
        }

        if (!matchCase) {
            dataString = dataString.toLowerCase()
            prepareRegex = prepareRegex.toLowerCase()
        }

        return Pattern.compile(prepareRegex).matcher(dataString).find()
    }
}

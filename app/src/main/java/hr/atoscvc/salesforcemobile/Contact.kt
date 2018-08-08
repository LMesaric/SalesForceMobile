package hr.atoscvc.salesforcemobile

data class Contact(
        var documentID: String,
        var status: Int,
        var title: Int,
        var firstName: String,
        var lastName: String,
        var company: String,   //TODO - Company type?
        var phone: String,
        var email: String,
        var details: String     //TODO - vrijeme komunikacije
)
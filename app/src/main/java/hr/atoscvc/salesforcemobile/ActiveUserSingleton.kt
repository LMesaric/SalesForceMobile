package hr.atoscvc.salesforcemobile

object ActiveUserSingleton {
    private var user: User? = null

    fun setActiveUser(user: User?) {
        this.user = user
    }

    fun getActiveUser(): User? {
        return this.user
    }

}
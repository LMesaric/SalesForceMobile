package hr.atoscvc.salesforcemobile

object CheckPasswordConstraints {
    fun checkPasswordConstraints(password: String): PasswordErrors {
        if (password.length < 8) {
            return PasswordErrors("Password must be at least 8 characters long.", false)
        }
        if (password.length > 72) {
            return PasswordErrors("Password cannot contain more than 72 characters.", false)
        }
        if (!password.matches(".*[a-z].*".toRegex())) {
            return PasswordErrors("Password must contain at least one lowercase letter.", false)
        }
        if (!password.matches(".*[A-Z].*".toRegex())) {
            return PasswordErrors("Password must contain at least one uppercase letter.", false)
        }
        if (!password.matches(".*\\d.*".toRegex())) {
            return PasswordErrors("Password must contain at least one digit.", false)
        }

        return PasswordErrors("Password is fine.", true)
    }
}
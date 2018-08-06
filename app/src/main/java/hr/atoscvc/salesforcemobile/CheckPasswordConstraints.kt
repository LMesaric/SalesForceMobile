package hr.atoscvc.salesforcemobile

object CheckPasswordConstraints {
    fun checkPasswordConstraints(user: String, pass: String): PasswordErrors {
        if (pass.length < 8) {
            return PasswordErrors("Password must be at least 8 characters long.", false)
        }
        if (pass.length > 72) {
            return PasswordErrors("Password cannot contain more than 72 characters.", false)
        }
        if (user.isNotEmpty() && pass.contains(user, true)) {
            return PasswordErrors("Password must not contain the username.", false)
        }
        if (user.contains(pass, true)) {
            return PasswordErrors("Username must not contain the password.", false)
        }
        if (!pass.matches(".*[a-z].*".toRegex())) {
            return PasswordErrors("Password must contain at least one lowercase letter.", false)
        }
        if (!pass.matches(".*[A-Z].*".toRegex())) {
            return PasswordErrors("Password must contain at least one uppercase letter.", false)
        }
        if (!pass.matches(".*\\d.*".toRegex())) {
            return PasswordErrors("Password must contain at least one digit.", false)
        }

        return PasswordErrors("Password is fine.", true)
    }
}
package it.unical.ea.lemu_frontend.models

data class UtenteRegistrationDto(
    val credenzialiEmail: String,
    val credenzialiPassword: String,
    val nome: String,
    val cognome: String
) {
    companion object {
        fun validateCredenzialiUsername(username: String): Boolean {
            return username.isNotEmpty()
        }

        fun validateCredenzialiEmail(email: String): Boolean {
            return email.isNotEmpty() && email.contains("@") && email.contains(".")
        }

        fun validateCredenzialiPassword(password: String): Boolean {
            return password.isNotEmpty() && password.length > 8
        }
    }
}


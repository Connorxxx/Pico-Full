package com.connor.picofull.models.type

sealed class BtnType {
    data class Energy(val value: Boolean): BtnType()

    data class Rate(val value: Boolean): BtnType()

    data class Start(val value: Boolean): BtnType()

    data class Login(val value: Boolean): BtnType()
}

inline fun BtnType.onEnergy(state: (Boolean) -> Unit) {
    if (this is BtnType.Energy) state(value)
}

inline fun BtnType.onRate(state: (Boolean) -> Unit) {
    if (this is BtnType.Rate) state(value)
}

inline fun BtnType.onStart(state: (Boolean) -> Unit) {
    if (this is BtnType.Start) state(value)
}

inline fun BtnType.onLogin(state: (Boolean) -> Unit) {
    if (this is BtnType.Login) state(value)
}

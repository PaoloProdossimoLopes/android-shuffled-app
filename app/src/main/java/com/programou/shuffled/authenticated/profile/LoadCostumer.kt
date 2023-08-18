package com.programou.shuffled.authenticated.profile

class LoadCostumer(private val session: Session): CostumerLoader {
    override fun loadCostumer(callback: (Costumer) -> Unit) {
        val costumer = session.getCostumer()
        if (!costumer.isValid()) throw InvalidCostumerInformationError()
        callback(costumer)
    }

    override fun logout() = session.logout()
}
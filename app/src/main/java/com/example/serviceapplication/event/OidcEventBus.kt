package com.example.serviceapplication.event

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class OidcEventBus {

    private val events = MutableSharedFlow<OidcEvent>()

    fun provideEvent( event: OidcEvent) {
        GlobalScope.launch {
            events.emit(event)
        }
    }

    suspend fun subscribeEvent( vararg oidcEvent: OidcEvent, eventHandler: () -> Unit) {
        events.filter {
            oidcEvent.contains(it)
        }.collect {
            eventHandler()
        }
    }


}
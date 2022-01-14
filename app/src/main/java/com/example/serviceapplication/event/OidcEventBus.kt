package com.example.serviceapplication.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OidcEventBus {

    private val events = MutableSharedFlow<OidcEvent>()

    suspend fun provideEvent( event: OidcEvent) {
        withContext(Dispatchers.IO) {
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
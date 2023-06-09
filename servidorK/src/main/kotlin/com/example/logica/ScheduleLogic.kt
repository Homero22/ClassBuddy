package com.example.logica

import com.example.data.models.Schedule
import com.example.data.repositories.Schedules
import com.example.data.repositories.cGenerica
import org.jetbrains.exposed.sql.Op
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

class ScheduleLogic {
    private val obj = cGenerica<Schedules>()

    fun crearHorario(objeto: Schedule): Int {
        Schedules.save(objeto)
        return 1
    }

    fun getAll(limit: Int, offset:Int):List<Any> {
        return obj.gGetAll(Schedules,limit,offset)
    }

    fun actualizarHorario(id: Int, schedule: Schedule): Int {
        val respuesta = obj.gGgetById(Schedules, id) ?: return 0
        obj.gUpdate(Schedules,id,schedule)
        return 1
    }

    fun eliminarHorario(id: Int): Int {
        val respuesta = obj.gGgetById(Schedules,id)
        if (respuesta != null) {
            obj.gDelete(Schedules,id)
            return 1
        }
        return 0
    }

    fun getById(id: Int): Any {
        return obj.gGgetById(Schedules,id) ?: return 0
    }

    fun getByUserId(id: Int): List<Any> {
        return obj.gGetByUserId(Schedules,id)
    }



}



package com.example.routes

import com.example.data.models.Note
import com.example.logica.NotesLogic
import com.example.services.Wpp.enviarWpp
import com.example.utils.Response
import com.example.utils.ResponseSimple
import com.example.utils.ResponseSingle
import com.example.utils.sendJsonResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.notesRouting(){
    route("/notes"){
        //Get /notes
        get {
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.parameters["offset"]?.toIntOrNull() ?: 0
            //envio a la capa logica
            val res = NotesLogic().getAll(limit,offset);
            if(res.isNotEmpty()){
                val response = Response(true,"Apuntes obtenidos correctamente", res)
                sendJsonResponse(call, HttpStatusCode.OK, response)
            }else{
                val response = Response(false,"No se encontraron apuntes", res)
                sendJsonResponse(call, HttpStatusCode.OK, response)
            }
        }
        post {
            //Obtenemos el apunte a guardar
            val apunte = call.receive<Note>()
            try{
                //envimaos a la capa logica
                val res = NotesLogic().save(apunte)
                if(res == 0){
                    val response = ResponseSimple(false,"No se pudo crear el apunte. Verifique que el título no esté repetido")
                    sendJsonResponse(call, HttpStatusCode.OK, response)
                }else{
                    val response = ResponseSingle(true,"Apunte creado correctamente", res)
                    sendJsonResponse(call, HttpStatusCode.OK, response)
                }
            }catch (
                cause: Throwable
            ){
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "Error desconocido")
            }
        }
        get("/{id}"){
            //Obtenemos el id del apunte a buscar
            val id = call.parameters["id"]?.toIntOrNull() ?: 0
            try {
                val res = NotesLogic().getById(id);
                if(res == null){
                    val response = ResponseSimple(false,"No se encontró el apunte")
                    sendJsonResponse(call, HttpStatusCode.OK, response)
                }else{
                    val response = ResponseSingle(true,"Apunte obtenido correctamente", res)
                    sendJsonResponse(call, HttpStatusCode.OK, response)
                }
            }catch (
                cause: Throwable
            ){
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "Error desconocido")
            }
        }
        get("/user/{id}"){
            //Obtenemos el id del usuario para obtener todos los apuntes
            val id = call.parameters["id"]?.toIntOrNull() ?: 0
            try {
                //enviamos a la capa logica
                val res = NotesLogic().getByUser(id);
                if(res.isNotEmpty()){
                    enviarWpp()
                    val response = Response(true,"Apuntes obtenidos correctamente", res)
                    sendJsonResponse(call, HttpStatusCode.OK, response)
                }else{
                    val response = Response(false,"No se encontraron apuntes", res)
                    sendJsonResponse(call, HttpStatusCode.OK, response)
                }
            }catch (
                cause: Throwable
            ){
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "Error desconocido")
            }
        }
        put("/{id}"){
            //Obtenemos el id del Apunte a Actualizar
            val id = call.parameters["id"]?.toIntOrNull() ?:0
            //Obtenemos el apunte a actualizar
            val note = call.receive<Note>()
            try{
                //enviamos a la capa logica
                when (val res = NotesLogic().update(id, note)) {
                    1 -> {
                        val response = ResponseSingle(true,"Apunte actualizado correctamente", res)
                        sendJsonResponse(call, HttpStatusCode.OK, response)
                    }
                    0 -> {
                        val response = ResponseSimple(false,"No se encontraron cambios en el apunte")
                        sendJsonResponse(call, HttpStatusCode.OK, response)
                    }
                    else -> {
                        val response = ResponseSimple(false,"No se encontró el apunte")
                        sendJsonResponse(call, HttpStatusCode.OK, response)
                    }
                }
            }catch (
                cause: Throwable
            ){
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "Error desconocido")
            }
        }
        delete("/{id}") {
            //Obtenemos el id del apunte a eliminar
            val id = call.parameters["id"]?.toIntOrNull() ?:0
            //Eliminamos el Apunte
            try {
                //enviamos a la capa logica
                val res = NotesLogic().delete(id)
                if(res == 1){
                    val response = ResponseSingle(true,"Apunte eliminado correctamente", res)
                    sendJsonResponse(call, HttpStatusCode.OK, response)
                }else{
                    val response = ResponseSingle(false,"No se pudo eliminar el apunte", res)
                    sendJsonResponse(call, HttpStatusCode.OK, response)
                }
            }catch (
                cause: Throwable
            ){
                call.respond(HttpStatusCode.BadRequest, cause.message ?: "Error desconocido")
            }
        }

    }
}
package com.example.data.repositories

import com.example.data.entities.HomeworkDAO
import com.example.data.entities.Homeworks
import com.example.data.entities.SubjectDAO
import com.example.data.entities.Subjects
import com.example.data.entities.Users
import com.example.data.models.Homework
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalTime
import kotlin.time.Duration.Companion.parse

/*
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.sql.Timestamp
*/

object Homeworks: CrudRepository<Homework, Int>() {
    //funcion para obtener todas las tareas
    override fun getAll(limit: Int, offset: Int): List<Homework> = transaction{
        val response = HomeworkDAO.all().limit(limit, offset.toLong())
        return@transaction response.map { it.toHomework() }
    }

    //funcion para obtener tarea por id
    override fun getById(id: Int) = transaction {
        return@transaction HomeworkDAO.findById(id.toLong())?.toHomework()
    }

    //funcion para guardar tarea
    override fun save(entity: Homework) = transaction{
        val response = HomeworkDAO.new {
            idUser= entity.idUser
            idMateria = entity.idMateria
            tareaTitulo = entity.tareaTitulo
            tareaDescripcion = entity.tareaDescripcion
            fechaCreacion = java.time.LocalDateTime.now()
            fechaFin = java.time.LocalDate.parse(entity.fechaFin)
            horaEntrega = java.time.LocalTime.parse(entity.horaEntrega)
            tareaEstado = entity.tareaEstado
            tareaRecordatorio = java.time.LocalTime.parse(entity.tareaRecordatorio)
        }
        return@transaction response.toHomework()
    }

    //funcion para actualizar tarea
    override fun update(id:Int, entity: Homework): Homework = transaction{
        val response = HomeworkDAO.findById(id.toLong())?.apply {
            tareaTitulo = entity.tareaTitulo
            tareaDescripcion = entity.tareaDescripcion
            fechaCreacion = java.time.LocalDateTime.now()
            fechaFin = java.time.LocalDate.parse(entity.fechaFin)
            horaEntrega = java.time.LocalTime.parse(entity.horaEntrega)
            tareaEstado = entity.tareaEstado
            tareaRecordatorio = java.time.LocalTime.parse(entity.tareaRecordatorio)
        }?.toHomework()
        getAllByUserAndStatePendientes(entity.idUser, entity.tareaEstado)
        return@transaction response!!
    }

    override fun delete(id:Int)= transaction {
        HomeworkDAO.findById(id.toLong())?.delete()
        return@transaction
    }

    //funcion para obtener todas las tareas de un usuario
    fun getAllByUser(id: Long):List<Any> = transaction {

        val res = Homeworks
            .select { Homeworks.idUser eq id }
            .orderBy(Pair(Homeworks.fechaFin, SortOrder.ASC))
            .map {
                mapOf(
                    "id" to it[Homeworks.id].value,
                    "idUser" to it[Homeworks.idUser],
                    "idMateria" to it[Homeworks.idMateria],
                    "nombreMateria" to SubjectDAO.get(it[Homeworks.idMateria]).nombre,
                    "tareaTitulo" to it[Homeworks.tareaTitulo],
                    "tareaDescripcion" to it[Homeworks.tareaDescripcion],
                    "fechaCreacion" to it[Homeworks.fechaCreacion],
                    "fechaFin" to it[Homeworks.fechaFin],
                    "horaEntrega" to it[Homeworks.horaEntrega],
                    "tareaEstado" to it[Homeworks.tareaEstado],
                    "tareaRecordatorio" to it[Homeworks.tareaRecordatorio]
                )
            }
        return@transaction res
    }

    //funcion para obtener todas las tareas de un usuario dado un estado

    fun getAllByUserAndState(id: Long, state: String):List<Any> = transaction {

        val res = Homeworks
            .select { Homeworks.idUser eq id }
            .andWhere { Homeworks.tareaEstado eq state }
            .orderBy(Pair(Homeworks.fechaFin, SortOrder.ASC))
            .map {
                mapOf(
                    "id" to it[Homeworks.id].value,
                    "idUser" to it[Homeworks.idUser],
                    "idMateria" to it[Homeworks.idMateria],
                    "tareaTitulo" to it[Homeworks.tareaTitulo],
                    "tareaDescripcion" to it[Homeworks.tareaDescripcion],
                    "fechaCreacion" to it[Homeworks.fechaCreacion],
                    "fechaFin" to it[Homeworks.fechaFin],
                    "horaEntrega" to it[Homeworks.horaEntrega],
                    "tareaEstado" to it[Homeworks.tareaEstado],
                    "tareaRecordatorio" to it[Homeworks.tareaRecordatorio]
                )
            }
        //ordenar por fecha de entrega

        return@transaction res
    }

    fun updateEstado(id:Int, estado :Int): Homework = transaction{
        if(estado == 1){
            val response = HomeworkDAO.findById(id.toLong())?.apply {
                tareaEstado = "PENDIENTE"
            }?.toHomework()
            return@transaction response!!
        }else{
            val response = HomeworkDAO.findById(id.toLong())?.apply {
                tareaEstado = "FINALIZADA"
            }?.toHomework()
            return@transaction response!!
        }
    }

    //funcion para obtener todas las tareas pendientes de un usuario y devolver in List <Homework>


    fun getAllByUserAndStatePendientes(id: Long, state: String):List<Homework> = transaction {
        val response = HomeworkDAO.all()
            .filter { it.idUser == id && it.tareaEstado == state }

        return@transaction response.map { it.toHomework() }
    }

    fun searchTitle(title: String):Int = transaction {
            val res = HomeworkDAO.find { Homeworks.tareaTitulo eq title }.toList()
            if(res.isEmpty()){
                return@transaction 0
            }else{
                return@transaction 1
            }
    }


    fun getPhoneById(id: Long): String = transaction {
        val response = Users
            .select { Users.id eq id }
            .map { it[Users.telefono] }
        return@transaction response[0]
    }

    fun getHomeworkByIdUserAndIdHomework(id: Long, idTarea: Int): String = transaction {
        val result = (Homeworks innerJoin Users)
            .select { Homeworks.idUser eq id and (Homeworks.id eq idTarea.toLong()) }
            .map { it[Homeworks.tareaTitulo] }
        println("OBTENCIOND DEL TITULO DE LA TAREA")
        println(result)
        return@transaction result[0]
    }
    fun getNameUserTarea(id: Long, idTarea: Int): String = transaction {
        val result = (Users innerJoin Homeworks)
            .select { Homeworks.idUser eq id and (Homeworks.id eq idTarea.toLong()) }
            .map { it[Users.nombre] }
        return@transaction result[0]
    }
    fun getHoraEntrega(id: Long, idTarea: Int): LocalTime = transaction {
        val result = (Homeworks innerJoin Users)
            .select { Homeworks.idUser eq id and (Homeworks.id eq idTarea.toLong())}
            .map { it[Homeworks.horaEntrega] }
        return@transaction result[0]
    }
    fun getAllByUserAndState(state: String): List<Homework> = transaction {
        val res = HomeworkDAO.all()
            .filter { it.tareaEstado == state }
        return@transaction res.map { it.toHomework() }
    }

}
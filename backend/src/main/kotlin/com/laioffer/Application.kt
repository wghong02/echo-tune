package com.laioffer

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
    // TODO: adding the routing configuration here
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/feed") {
            val jsonString = this::class.java.classLoader.getResource("feed.json")?.readText()
            call.respondText(jsonString ?: "", ContentType.Application.Json)

        }

        get("/playlist") {
            val jsonString = this::class.java.classLoader.getResource("playlists.json")?.readText()
            call.respondText(jsonString ?: "", ContentType.Application.Json)

        }

        get("/playlist/{id}") {
            val id = call.parameters["id"]
            val jsonString: String? = this::class.java.classLoader.getResource("playlists.json")?.readText()

            jsonString?.let {
                val playlists = Json.decodeFromString(ListSerializer(Playlist.serializer()), jsonString)
                val playlist = playlists.firstOrNull{ p-> p.id.toString() == id}
                call.respondNullable((playlist))
            } ?: call.respond("null")

//            for (playlist in playlists){
//                if (playlist.id.toString() == id){
//                    call.respond(playlist)
//                }
//            }
        }

        static("/") {
            staticBasePackage = "static"
            static("songs") {
                resources("songs")
            }
        }
    }
}

@Serializable
data class Playlist (
    val id: Long,
    val songs: List<Song>
)

@Serializable
data class Song(
    val name: String,
    val lyric: String,
    val src: String,
    val length: String
)
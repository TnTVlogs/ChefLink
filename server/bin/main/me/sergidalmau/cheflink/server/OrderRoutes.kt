package me.sergidalmau.cheflink.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.delete
import io.ktor.server.routing.put
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import me.sergidalmau.cheflink.data.repository.OrderRepository
import me.sergidalmau.cheflink.data.repository.ProductRepositoryImpl
import me.sergidalmau.cheflink.data.repository.TableRepositoryImpl
import me.sergidalmau.cheflink.data.repository.UserRepositoryImpl
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.domain.models.UserRole

private val orderRepository = OrderRepository()
private val userRepository = UserRepositoryImpl()
private val tableRepository = TableRepositoryImpl()
private val productRepository = ProductRepositoryImpl()

private val updateFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

suspend fun notifyClients() {
    println("Server: Broadcasting update via SharedFlow")
    updateFlow.emit(Unit)
}

fun Route.ordersRoutes(tokenManager: TokenManager) {

    get("/health") {
        println("Server: Health check hit")
        call.respondText("OK")
    }

    // Public auth routes
    post("/login") {
        try {
            val credentials = call.receive<Map<String, String>>()
            val username = credentials["username"] ?: ""
            val password = credentials["password"] ?: ""
            
            val user = userRepository.login(username, password)
            if (user != null) {
                val accessToken = tokenManager.generateAccessToken(user.id)
                val refreshToken = tokenManager.generateRefreshToken(user.id)
                
                // Persist refresh token in DB
                userRepository.saveRefreshToken(user.id, refreshToken, System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)
                
                call.respond(AuthResponse(accessToken, refreshToken, user))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Credencials incorrectes")
            }
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/refresh") {
        try {
            val request = call.receive<RefreshRequest>()
            val userId = userRepository.validateRefreshToken(request.refreshToken)
            
            if (userId != null) {
                val newAccessToken = tokenManager.generateAccessToken(userId)
                // Optional: Rotate refresh token here too if desired, but for now just new access token
                call.respond(mapOf("accessToken" to newAccessToken))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid or expired refresh token")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/logout") {
        try {
            val request = call.receive<RefreshRequest>() // Client sends refresh token to revoke it
            userRepository.revokeRefreshToken(request.refreshToken)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/register") {
        try {
            val data = call.receive<Map<String, String>>()
            val user = userRepository.register(
                data["username"] ?: "",
                data["password"] ?: "",
                data["firstName"] ?: "",
                data["lastName"] ?: "",
                data["email"] ?: "",
                UserRole.valueOf(data["role"] ?: "Cambrer")
            )
            call.respond(HttpStatusCode.Created, user)
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    // Protected routes
    authenticate("auth-jwt") {
        
        route("/orders") {
            webSocket("/updates") {
                println("Server: New WebSocket session established")
                val job = launch {
                    updateFlow.collect {
                        try {
                            send(Frame.Text("orders_updated"))
                            println("Server: Update notification sent to client")
                        } catch (e: Exception) {
                            println("Server: Error sending to client, closing session: ${e.message}")
                            this@webSocket.close()
                        }
                    }
                }
                try {
                    for (frame in incoming) {
                    }
                } catch (e: Exception) {
                    println("Server: WebSocket error/session closed: ${e.message}")
                } finally {
                    println("Server: WebSocket session closed, cleaning up job")
                    job.cancel()
                }
            }

            get {
                try {
                    val orders = orderRepository.getPendingOrders()
                    call.respond(orders)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }

            post {
                try {
                    val order = call.receive<Order>()
                    orderRepository.createOrder(order)
                    notifyClients()
                    call.respond(HttpStatusCode.Created, "Comanda guardada")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error format: ${e.message}")
                }
            }

            post("/{id}/status") {
                try {
                    val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val newStatus = call.receive<OrderStatus>()
                    orderRepository.updateOrderStatus(id, newStatus)
                    notifyClients()
                    call.respond(HttpStatusCode.OK)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            delete("/{id}") {
                try {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    orderRepository.deleteOrder(id)
                    notifyClients()
                    call.respond(HttpStatusCode.OK)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

        post("/users/{id}/password") {
            try {
                val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val data = call.receive<Map<String, String>>()
                val success = userRepository.changePassword(id, data["oldPassword"] ?: "", data["newPassword"] ?: "")
                if (success) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.BadRequest)
            } catch (_: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        route("/tables") {
            get {
                try {
                    val tables = tableRepository.getTables()
                    call.respond(tables)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            post {
                try {
                    val data = call.receive<Map<String, Int>>()
                    val number = data["number"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing number")
                    val capacity = data["capacity"] ?: 4
                    val table = tableRepository.createTable(number, capacity)
                    notifyClients()
                    call.respond(HttpStatusCode.Created, table)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error al crear la taula")
                }
            }

            put("/{number}") {
                try {
                    val number =
                        call.parameters["number"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val data = call.receive<Map<String, Int>>()
                    val capacity = data["capacity"] ?: 4
                    tableRepository.updateTable(number, capacity)
                    notifyClients()
                    call.respond(HttpStatusCode.OK)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            delete("/{number}") {
                try {
                    val number =
                        call.parameters["number"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    tableRepository.deleteTable(number)
                    notifyClients()
                    call.respond(HttpStatusCode.OK)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

        route("/products") {
            get {
                try {
                    val products = productRepository.getProducts()
                    call.respond(products)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            post {
                try {
                    val data = call.receive<Map<String, String>>()
                    val name = data["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing name")
                    val category = ProductCategory.valueOf(data["category"] ?: "Primers")
                    val price = data["price"]?.toDoubleOrNull() ?: 0.0
                    val description = data["description"]
                    val isAvailable = data["isAvailable"]?.toBooleanStrictOrNull() ?: true
                    val product = productRepository.createProduct(name, category, price, description, isAvailable)
                    notifyClients()
                    call.respond(HttpStatusCode.Created, product)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error al crear article: ${e.message}")
                }
            }

            put("/{id}") {
                try {
                    val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val data = call.receive<Map<String, String>>()
                    val name = data["name"] ?: ""
                    val category = ProductCategory.valueOf(data["category"] ?: "Primers")
                    val price = data["price"]?.toDoubleOrNull() ?: 0.0
                    val description = data["description"]
                    val isAvailable = data["isAvailable"]?.toBooleanStrictOrNull() ?: true
                    productRepository.updateProduct(id, name, category, price, description, isAvailable)
                    notifyClients()
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }

            delete("/{id}") {
                try {
                    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                    productRepository.deleteProduct(id)
                    notifyClients()
                    call.respond(HttpStatusCode.OK)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}

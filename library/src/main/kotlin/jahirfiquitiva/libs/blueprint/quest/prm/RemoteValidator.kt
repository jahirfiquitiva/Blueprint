package jahirfiquitiva.libs.blueprint.quest.prm

import com.afollestad.bridge.Response
import com.afollestad.bridge.ResponseValidator
import jahirfiquitiva.libs.kauextensions.extensions.string

/**
 * @author Aidan Follestad (afollestad)
 */
class RemoteValidator : ResponseValidator() {
    @Throws(Exception::class)
    override fun validate(response: Response): Boolean {
        val body = response.asString().orEmpty()
        if (body.startsWith("{")) {
            val json = response.asJsonObject()
            json?.let {
                if (it.string("status") != "success") {
                    throw Exception(json.string("error"))
                }
            }
        }
        return true
    }
    
    override fun id(): String = "backend-validator"
}

package jahirfiquitiva.libs.blueprint.quest.prm

import com.afollestad.bridge.Response
import com.afollestad.bridge.ResponseValidator

/**
 * @author Aidan Follestad (afollestad)
 */
class RemoteValidator : ResponseValidator() {
    @Throws(Exception::class)
    override fun validate(response: Response): Boolean {
        val body = response.asString()
        if (body != null && body.startsWith("{")) {
            val json = response.asJsonObject()
            if (json!!.getString("status") != "success")
                throw Exception(json.getString("error"))
        }
        return true
    }
    
    override fun id(): String = "backend-validator"
}

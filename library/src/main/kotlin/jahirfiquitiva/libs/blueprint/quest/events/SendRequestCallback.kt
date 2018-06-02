package jahirfiquitiva.libs.blueprint.quest.events

@Suppress("unused", "UNUSED_PARAMETER")
abstract class SendRequestCallback {
    abstract fun doWhenStarted()
    
    abstract fun doOnError()
    
    open fun updateWithProgress(progress: Int) {}
    
    abstract fun doWhenReady(forArctic: Boolean)
}
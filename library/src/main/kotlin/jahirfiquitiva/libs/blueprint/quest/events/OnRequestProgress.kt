package jahirfiquitiva.libs.blueprint.quest.events

@Suppress("unused", "UNUSED_PARAMETER")
abstract class OnRequestProgress {
    abstract fun doWhenStarted()

    abstract fun doOnError()

    fun updateWithProgress(progress: Int) {}

    abstract fun doWhenReady()
}
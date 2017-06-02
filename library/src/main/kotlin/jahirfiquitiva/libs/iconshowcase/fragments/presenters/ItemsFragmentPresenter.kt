/*
 * Copyright (c) 2017.  Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 *   https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.fragments.presenters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity

interface ItemsFragmentPresenter<D>:BasicFragmentPresenter {

    fun executeTask(context:Context) {
        if (context is AppCompatActivity) {
            try {
                context.supportLoaderManager.getLoader<D>(getLoaderId()).stopLoading()
                context.supportLoaderManager.destroyLoader(getLoaderId())
            } catch (ignored:Exception) {
            }
            try {
                context.supportLoaderManager.initLoader(getLoaderId(), null, getLoaderCallbacks())
            } catch (ignored:Exception) {
            }
        }
    }

    private fun getLoaderCallbacks():LoaderManager.LoaderCallbacks<D> {
        return object:LoaderManager.LoaderCallbacks<D> {
            override fun onCreateLoader(id:Int, args:Bundle?):Loader<D> = buildLoader()

            override fun onLoadFinished(loader:Loader<D>?, data:D) {
                onDataLoadFinished(data)
            }

            override fun onLoaderReset(loader:Loader<D>?) {
                // Do nothing
            }
        }
    }

    fun getLoaderId():Int
    fun buildLoader():Loader<D>
    fun onDataLoadFinished(data:D)
    fun onItemClicked(item:Any)
}
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

package jahirfiquitiva.libs.iconshowcase.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.adapters.IconsAdapter
import jahirfiquitiva.libs.iconshowcase.models.Icon
import jahirfiquitiva.libs.iconshowcase.tasks.BasicTaskLoader
import jahirfiquitiva.libs.iconshowcase.tasks.LoadIcons
import jahirfiquitiva.libs.iconshowcase.ui.views.EmptyViewRecyclerView

class IconsFragment:Fragment() {

    var recyclerView:EmptyViewRecyclerView? = null
    var fastScroller:RecyclerFastScroller? = null

    override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?,
                              savedInstanceState:Bundle?):View? {

        super.onCreateView(inflater, container, savedInstanceState)
        val content = inflater?.inflate(R.layout.extra_icons_grid, container, false) as View
        initRV(content)
        return content
    }

    private fun executeTask(task:LoadIcons,
                            callbacks:LoaderManager.LoaderCallbacks<ArrayList<Icon>>) {
        try {
            (context as AppCompatActivity).supportLoaderManager.getLoader<ArrayList<Icon>>(
                    task.getTaskId()).cancelLoad()
            (context as AppCompatActivity).supportLoaderManager.destroyLoader(task.getTaskId())
            (context as AppCompatActivity).supportLoaderManager.initLoader(task.getTaskId(), null,
                    callbacks)
        } catch (ignored:Exception) {
        }
    }

    private fun initRV(content:View) {
        recyclerView = content.findViewById(R.id.icons_grid) as EmptyViewRecyclerView
        fastScroller = content.findViewById(R.id.fast_scroller) as RecyclerFastScroller

        /*
        val icons = LoadIcons(context,
                object:BasicTaskLoader.TaskListener<ArrayList<Icon>> {
                    override fun onTaskStarted(task:BasicTaskLoader<ArrayList<Icon>>) {
                        recyclerView?.state = EmptyViewRecyclerView.STATE_LOADING
                        recyclerView?.updateStateViews()
                    }

                    override fun onTaskCompleted(task:BasicTaskLoader<ArrayList<Icon>>) {
                        initRecyclerViewAdapter(task.data!!)
                    }
                })
        try {
            (context as AppCompatActivity).supportLoaderManager.getLoader<ArrayList<Icon>>(
                    icons.getTaskId()).cancelLoad()
            (context as AppCompatActivity).supportLoaderManager.destroyLoader(icons.getTaskId())
            /*
            (context as AppCompatActivity).supportLoaderManager.initLoader(icons.getTaskId(), null,
                    object:LoaderManager.LoaderCallbacks<ArrayList<Icon>> {
                        override fun onCreateLoader(id:Int, args:Bundle?):Loader<ArrayList<Icon>>? {
                            return LoadIcons(context,
                                    object:BasicTaskLoader.TaskListener<ArrayList<Icon>>{
                                        override fun onTaskCompleted(
                                                task:BasicTaskLoader<ArrayList<Icon>>) {
                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                        }

                                        override fun onTaskStarted(
                                                task:BasicTaskLoader<ArrayList<Icon>>) {
                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                        }
                                    })
                        }

                        override fun onLoadFinished(loader:Loader<ArrayList<Icon>>?,
                                                    data:ArrayList<Icon>?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onLoaderReset(loader:Loader<ArrayList<Icon>>?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })
                    */
        } catch (ignored:Exception) {
        }
        */


        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.textView = content.findViewById(R.id.empty_text) as TextView?
        recyclerView?.state = EmptyViewRecyclerView.STATE_LOADING
        recyclerView?.updateStateViews()
    }

    private fun initRecyclerViewAdapter(icons:ArrayList<Icon>) {
        recyclerView?.adapter = IconsAdapter(icons) {}
    }


}
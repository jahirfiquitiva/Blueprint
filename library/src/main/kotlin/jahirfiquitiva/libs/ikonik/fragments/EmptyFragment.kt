/*
 * Copyright (c) 2017. Jahir Fiquitiva
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
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.fragments.presenters.BasicFragmentPresenter
import jahirfiquitiva.libs.blueprint.ui.views.EmptyViewRecyclerView

open class EmptyFragment:Fragment(), BasicFragmentPresenter {
    override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?,
                              savedInstanceState:Bundle?):View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val content:View = inflater?.inflate(R.layout.section_empty, container, false) as View
        initUI(content)
        return content
    }

    override fun initUI(content:View) {
        val emptyRecyclerView:EmptyViewRecyclerView = content.findViewById(R.id.empty_rv)
        emptyRecyclerView.emptyView = content.findViewById(R.id.empty_view)
        emptyRecyclerView.textView = content.findViewById(R.id.empty_text)
        emptyRecyclerView.updateState(EmptyViewRecyclerView.STATE_EMPTY)
    }
}
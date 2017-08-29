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
 */

package jahirfiquitiva.libs.blueprint.ui.fragments

import android.view.View
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.frames.ui.fragments.base.BasicFragment
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView

open class EmptyFragment:BasicFragment<Boolean>() {
    override fun getContentLayout():Int = R.layout.section_wo_fastscroll
    
    override fun initUI(content:View) {
        val emptyRecyclerView:EmptyViewRecyclerView = content.findViewById(R.id.section_rv)
        emptyRecyclerView.emptyView = content.findViewById(R.id.empty_view)
        emptyRecyclerView.textView = content.findViewById(R.id.empty_text)
        emptyRecyclerView.state = EmptyViewRecyclerView.State.EMPTY
    }
    
    override fun onItemClicked(item:Boolean) {}
}
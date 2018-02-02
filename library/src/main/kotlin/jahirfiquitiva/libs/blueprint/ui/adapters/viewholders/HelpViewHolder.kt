/*
 * Copyright (c) 2018. Jahir Fiquitiva
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
package jahirfiquitiva.libs.blueprint.ui.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.ui.adapters.HelpItem
import jahirfiquitiva.libs.kauextensions.extensions.bind

class HelpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val question: TextView? by itemView.bind(R.id.help_question)
    private val answer: TextView? by itemView.bind(R.id.help_answer)
    fun setQA(help: HelpItem) {
        question?.text = help.question
        answer?.text = help.answer
    }
}
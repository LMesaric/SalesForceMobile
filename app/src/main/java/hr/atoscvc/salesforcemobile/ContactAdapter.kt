package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.os.SystemClock
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_layout_contacts.view.*

class ContactAdapter(
        private var contactList: ArrayList<Contact>,
        private val context: Activity,
        private val isForSelect: Boolean,
        private val listenerContacts: RecyclerViewContactsOnClickListener
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var lastClickTime: Long = 0

    interface RecyclerViewContactsOnClickListener {
        fun recyclerViewContactsOnClick(
                circleImageView: CircleImageView,
                contact: Contact,
                hideEditButtons: Boolean
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.list_layout_contacts,
                parent,
                false
        )
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact: Contact = contactList[position]

        holder.tvCardContactName.text = ConcatenateObjectToString.concatenateContactName(contact, context.resources, true)
        holder.tvCardContactCompany.text = contact.company?.name ?: context.getString(R.string.noCompanyFoundError)
        holder.tvCardContactStatus.text = context.resources.getStringArray(R.array.status_array)[contact.status]

        holder.constraintLayoutContactMain.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime >= 1000) {
                lastClickTime = SystemClock.elapsedRealtime()
                listenerContacts.recyclerViewContactsOnClick(
                        holder.ivContactAvatar,
                        contactList[position],
                        isForSelect
                )
            }
        }

        if (isForSelect) {
            holder.btnCardContactSelectContact.visibility = View.VISIBLE
            holder.btnCardContactSelectContact.isEnabled = true

            holder.btnCardContactSelectContact.setOnClickListener {
                //TODO Use this for creating opportunities
                val intent = Intent().apply {
                    putExtra(context.getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
                }
                context.setResult(Activity.RESULT_OK, intent)
            }
        } else {
            holder.btnCardContactSelectContact.visibility = View.GONE
            holder.btnCardContactSelectContact.isEnabled = false
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun filter(filteredList: ArrayList<Contact>) {
        contactList = filteredList
        notifyDataSetChanged()
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardContactName: TextView = itemView.tvCardContactName
        val tvCardContactCompany: TextView = itemView.tvCardContactCompany
        val tvCardContactStatus: TextView = itemView.tvCardContactStatus

        val ivContactAvatar: CircleImageView = itemView.ivContactAvatar
        val btnCardContactSelectContact: Button = itemView.btnCardContactSelectContact
        val constraintLayoutContactMain: ConstraintLayout = itemView.constraintLayoutContactMain
    }
}

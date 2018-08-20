package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_layout_contacts.view.*

class ContactAdapter(private val contactList: ArrayList<Contact>, val context: Activity, private val isForSelect: Boolean, private val listener: RecyclerViewOnClickListener) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var currentPosition = -1    // If -1 is replaced with 0 then the first card will automatically be expanded
    private var isExpanded = false

    interface RecyclerViewOnClickListener {
        fun recyclerViewOnClick(circleImageView: CircleImageView, contact: Contact, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.list_layout_contacts,
                parent,
                false
        )
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        if (position == 0) {
            holder.marginView.visibility = View.VISIBLE
            holder.constraintLayoutContactsMain.visibility = View.GONE
        } else {
            holder.marginView.visibility = View.GONE
            holder.constraintLayoutContactsMain.visibility = View.VISIBLE

            val contact: Contact = contactList[position - 1]
            val contactTitle: String = if (contact.title == 0) {
                ""
            } else {
                context.resources.getStringArray(R.array.contactTitle_array)[contact.title] + " "
            }
            val tvCardContactsNameText = "$contactTitle${contact.firstName} ${contact.lastName}"

            holder.tvCardContactsName.text = tvCardContactsNameText
            holder.tvCardContactCompany.text = contact.company?.name ?: "No company found"
            holder.tvCardContactStatus.text = context.resources.getStringArray(R.array.status_array)[contact.status]
            /**holder.tvCardContactsStatus.text = context.resources.getStringArray(R.array.status_array)[contact.status]
            holder.tvCardContactsPrefTime.text = context.resources.getStringArray(R.array.contactPreferredTime_array)[contact.preferredTime]
            holder.tvCardContactsPhone.text = contact.phone
            holder.tvCardContactsEmail.text = contact.email
            holder.tvCardContactsDetails.text = contact.details*/

            if (currentPosition == position) {
                //holder.constraintLayoutContactsExpandable.visibility = View.VISIBLE
                if (!isExpanded) {
//                val slideDown = AnimationUtils.loadAnimation(context, R.anim.animation_cardview_expand)
                    //holder.constraintLayoutContactsExpandable.visibility = View.VISIBLE
//                holder.constraintLayoutContactsExpandable.startAnimation(slideDown)
                } else {
//                val slideUp = AnimationUtils.loadAnimation(context, R.anim.animation_cardview_collapse)
                    //holder.constraintLayoutContactsExpandable.visibility = View.GONE
//                holder.constraintLayoutContactsExpandable.startAnimation(slideUp)
                }
            } else {    // Remove this else statement and the other cards will not automatically collapse
                //holder.constraintLayoutContactsExpandable.visibility = View.GONE
            }

            holder.constraintLayoutContactsMain.setOnClickListener {
                currentPosition = holder.adapterPosition

                listener.recyclerViewOnClick(holder.ivContactAvatar, contactList[currentPosition - 1], currentPosition - 1)

                /*if (holder.constraintLayoutContactsExpandable.visibility == View.GONE) {
                    isExpanded = false
                    Log.i("PROBA", "RADI")
                } else {
                    isExpanded = true
                }
                notifyDataSetChanged()*/
            }

            /* if (isForSelect) {
                 //holder.btnCardContactsEditContact.visibility = View.GONE
                 //holder.btnCardContactsSelectContact.visibility = View.VISIBLE

                 //holder.btnCardContactsSelectContact.setOnClickListener {
                     //TODO Use this for creating opportunities
                     val intent = Intent()
                     intent.putExtra(context.getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
                     context.setResult(Activity.RESULT_OK, intent)
                     context.finish()
                 //}
             } else {
                 //holder.btnCardContactsEditContact.visibility = View.VISIBLE
                 //holder.btnCardContactsSelectContact.visibility = View.GONE

                 holder.btnCardContactsEditContact.setOnClickListener {
                     val intent = Intent(context, ContactEditorActivity::class.java).apply {
                         putExtra(context.getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)
                         putExtra(context.getString(R.string.EXTRA_CONTACT_ENTIRE_OBJECT), contact)
                     }
                     ContactEditFragment.chosenCompany = contact.company
                     context.startActivityForResult(intent, ContactsFragment.requestCodeRefresh)
                     //TODO Testirati radi li implementirani refresh RecycleViewa nakon Savea
                 }
             }*/
        }
    }

    override fun getItemCount(): Int {
        return contactList.size + 1
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val marginView: View = itemView.marginViewContacts

        val tvCardContactsName: TextView = itemView.tvCardContactsName
        val tvCardContactCompany: TextView = itemView.tvCardContactCompany

        val ivContactAvatar: CircleImageView = itemView.ivContactAvatar

        //val tvCardContactsStatus: TextView = itemView.tvCardContactsStatus
        //val tvCardContactsPrefTime: TextView = itemView.tvCardContactsPrefTime
        //val tvCardContactsPhone: TextView = itemView.tvCardContactsPhone
        //val tvCardContactsEmail: TextView = itemView.tvCardContactsEmail
        //val tvCardContactsDetails: TextView = itemView.tvCardContactsDetails

        val constraintLayoutContactsMain: ConstraintLayout = itemView.constraintLayoutContactsMain
        val tvCardContactStatus: TextView = itemView.tvCardContactStatus
        //val constraintLayoutContactsExpandable: ConstraintLayout = itemView.constraintLayoutContactsExpandable

        //val btnCardContactsEditContact: Button = itemView.btnCardContactsEditContact
        //val btnCardContactsSelectContact: Button = itemView.btnCardContactsSelectContact
    }
}
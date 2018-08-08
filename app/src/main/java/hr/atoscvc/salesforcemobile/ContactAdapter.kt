package hr.atoscvc.salesforcemobile

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_layout_contacts.view.*

class ContactAdapter(private val contactList: ArrayList<Contact>, val context: Context) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_layout_contacts, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        val tvCardContactsNameText = "${context.resources.getStringArray(R.array.contactTitle_array)[contact.title]} ${contact.firstName} ${contact.lastName}"
        holder.tvCardContactsName.text = tvCardContactsNameText
        holder.tvCardContactCompany.text = contact.company
        holder.tvCardContactsStatus.text = context.resources.getStringArray(R.array.status_array)[contact.status]
        holder.tvCardContactsPhone.text = contact.phone
        holder.tvCardContactsEmail.text = contact.email
        holder.tvCardContactsDetails.text = contact.details

        if (currentPosition == position) {
            if (holder.constraintLayoutContactsExpandable.visibility == View.GONE) {
//                val slideDown = AnimationUtils.loadAnimation(context, R.anim.animation_cardview_expand)
                holder.constraintLayoutContactsExpandable.visibility = View.VISIBLE
//                holder.constraintLayoutContactsExpandable.startAnimation(slideDown)
            } else {
//                val slideUp = AnimationUtils.loadAnimation(context, R.anim.animation_cardview_collapse)
                holder.constraintLayoutContactsExpandable.visibility = View.GONE
//                holder.constraintLayoutContactsExpandable.startAnimation(slideUp)
            }
        }

        holder.constraintLayoutContactsMain.setOnClickListener {
            currentPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardContactsName: TextView = itemView.tvCardContactsName
        val tvCardContactCompany: TextView = itemView.tvCardContactCompany

        val tvCardContactsStatus: TextView = itemView.tvCardContactsStatus
        val tvCardContactsPhone: TextView = itemView.tvCardContactsPhone
        val tvCardContactsEmail: TextView = itemView.tvCardContactsEmail
        val tvCardContactsDetails: TextView = itemView.tvCardContactsDetails

        val constraintLayoutContactsMain: ConstraintLayout = itemView.constraintLayoutContactsMain
        val constraintLayoutContactsExpandable: ConstraintLayout = itemView.constraintLayoutContactsExpandable
    }

}
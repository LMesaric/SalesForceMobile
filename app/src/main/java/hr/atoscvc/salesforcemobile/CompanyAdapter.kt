package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.list_layout_companies.view.*

class CompanyAdapter(private val companyList: ArrayList<Company>, val context: Context, private val isForSelect: Boolean) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    private var currentPosition = -1    // If -1 is replaced with 0 then the first card will automatically be expanded

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.list_layout_companies,
                parent,
                false
        )
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companyList[position]
        //LUKA - outOfBounds za Company i Contact
        holder.tvCardCompaniesName.text = company.name
        holder.tvCardCompaniesCvsSegment.text = context.resources.getStringArray(R.array.companyCVS_array)[company.cvsSegment]
        holder.tvCardCompaniesStatus.text = context.resources.getStringArray(R.array.status_array)[company.status]
        holder.tvCardCompaniesOIB.text = company.OIB
        holder.tvCardCompaniesWebPage.text = company.webPage
        holder.tvCardCompaniesPhone.text = company.phone
        holder.tvCardCompaniesIncome.text = company.income
        holder.tvCardCompaniesCommunicationType.text = context.resources.getStringArray(R.array.companyCommunicationType_array)[company.communicationType]
        holder.tvCardCompaniesEmployees.text = context.resources.getStringArray(R.array.companyEmployees_array)[company.employees]
        holder.tvCardCompaniesDetails.text = company.details

        if (currentPosition == position) {
            if (holder.constraintLayoutCompaniesExpandable.visibility == View.GONE) {
//                val slideDown = AnimationUtils.loadAnimation(context, R.anim.animation_cardview_expand)
                holder.constraintLayoutCompaniesExpandable.visibility = View.VISIBLE
//                holder.constraintLayoutCompaniesExpandable.startAnimation(slideDown)
            } else {
//                val slideUp = AnimationUtils.loadAnimation(context, R.anim.animation_cardview_collapse)
                holder.constraintLayoutCompaniesExpandable.visibility = View.GONE
//                holder.constraintLayoutCompaniesExpandable.startAnimation(slideUp)
            }
        } else {    // Remove this else statement and the other cards will not automatically collapse
            holder.constraintLayoutCompaniesExpandable.visibility = View.GONE
        }

        holder.constraintLayoutCompaniesMain.setOnClickListener {
            currentPosition = holder.adapterPosition
            notifyDataSetChanged()
        }

        if (isForSelect) {
            holder.btnCardCompaniesAddContact.visibility = View.GONE
            holder.btnCardCompaniesEditCompany.visibility = View.GONE
            holder.btnCardCompaniesSelectCompany.visibility = View.VISIBLE

            holder.btnCardCompaniesSelectCompany.setOnClickListener {
                val intent = Intent()
                intent.putExtra(context.getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
                (context as Activity).setResult(RESULT_OK, intent)
                context.finish()
            }
        } else {
            holder.btnCardCompaniesAddContact.visibility = View.VISIBLE
            holder.btnCardCompaniesEditCompany.visibility = View.VISIBLE
            holder.btnCardCompaniesSelectCompany.visibility = View.GONE

            holder.btnCardCompaniesAddContact.setOnClickListener {
                val intent = Intent(context, ContactEditorActivity::class.java).apply {
                    putExtra(context.getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)
                }
                context.startActivity(intent)
            }

            holder.btnCardCompaniesEditCompany.setOnClickListener {
                val intent = Intent(context, CompanyEditorActivity::class.java).apply {
                    putExtra(context.getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), false)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_ENTIRE_OBJECT), company)

                    /*putExtra(context.getString(R.string.EXTRA_COMPANY_DOCUMENT_ID), company.documentID)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_STATUS_SPINNER_INDEX), company.status)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_CVS_SPINNER_INDEX), company.cvsSegment)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_COMMUNICATION_TYPE_SPINNER_INDEX), company.communicationType)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_EMPLOYEES_SPINNER_INDEX), company.employees)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_NAME), company.name)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_OIB), company.OIB)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_WEB_PAGE), company.webPage)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_PHONE), company.phone)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_DETAILS), company.details)
                    putExtra(context.getString(R.string.EXTRA_COMPANY_INCOME), company.income)*/
                }
                (context as Activity).startActivityForResult(intent, CompanyListActivity.requestCodeRefresh)
                //TODO Testirati radi li implementirani refresh RecycleViewa nakon Savea
            }
        }
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardCompaniesName: TextView = itemView.tvCardCompaniesName
        val tvCardCompaniesCvsSegment: TextView = itemView.tvCardCompaniesCvsSegment

        val tvCardCompaniesStatus: TextView = itemView.tvCardCompaniesStatus
        val tvCardCompaniesOIB: TextView = itemView.tvCardCompaniesOIB
        val tvCardCompaniesWebPage: TextView = itemView.tvCardCompaniesWebPage
        val tvCardCompaniesPhone: TextView = itemView.tvCardCompaniesPhone
        val tvCardCompaniesIncome: TextView = itemView.tvCardCompaniesIncome
        val tvCardCompaniesCommunicationType: TextView = itemView.tvCardCompaniesCommunicationType
        val tvCardCompaniesEmployees: TextView = itemView.tvCardCompaniesEmployees
        val tvCardCompaniesDetails: TextView = itemView.tvCardCompaniesDetails

        val constraintLayoutCompaniesMain: ConstraintLayout = itemView.constraintLayoutCompaniesMain
        val constraintLayoutCompaniesExpandable: ConstraintLayout = itemView.constraintLayoutCompaniesExpandable

        val btnCardCompaniesAddContact: Button = itemView.btnCardCompaniesAddContact
        val btnCardCompaniesEditCompany: Button = itemView.btnCardCompaniesEditCompany
        val btnCardCompaniesSelectCompany: Button = itemView.btnCardCompaniesSelectCompany
    }
}
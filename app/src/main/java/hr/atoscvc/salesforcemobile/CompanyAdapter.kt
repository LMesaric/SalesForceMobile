package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.list_layout_companies.view.*


class CompanyAdapter(private val companyList: ArrayList<Company>, val context: Activity, private val isForSelect: Boolean) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

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
        if (position == 0) {
            holder.marginView.visibility = View.VISIBLE
            holder.cardView.visibility = View.GONE
        } else {
            holder.marginView.visibility = View.GONE
            holder.cardView.visibility = View.VISIBLE

            val company = companyList[position - 1]
            //LUKA - outOfBounds za Company i Contact files
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
                    context.setResult(RESULT_OK, intent)
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
                    }
                    (context).startActivityForResult(intent, CompaniesFragment.requestCodeRefresh)
                    //TODO Testirati radi li implementirani refresh RecycleViewa nakon Savea
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return companyList.size + 1
    }

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val marginView: View = itemView.marginViewCompanies

        val cardView: CardView = itemView.cardViewCompanies

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
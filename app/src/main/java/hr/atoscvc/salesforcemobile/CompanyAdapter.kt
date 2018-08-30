package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.os.SystemClock
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.list_layout_companies.view.*

class CompanyAdapter(
        private var companyList: ArrayList<Company>,
        private val context: Activity,
        private val isForSelect: Boolean,
        private val listenerCompanies: RecyclerViewCompaniesOnClickListener
) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    private var generator = ColorGenerator.MATERIAL

    private var lastClickTime: Long = 0

    interface RecyclerViewCompaniesOnClickListener {
        //FILIP - sortirati listu (Companies & Contacts) i onda pozvati notifyDataSetChanged(), pa se ne treba loadati ponovno iz baze
        fun recyclerViewCompaniesOnClick(
                imageView: ImageView,
                company: Company,
                hideEditButtons: Boolean
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.list_layout_companies,
                parent,
                false
        )
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company: Company = companyList[position]

        val letters = company.name[0].toString().toUpperCase()
        val drawable: TextDrawable = TextDrawable.builder().buildRound(letters, generator.getColor(company.documentID))
        holder.ivCompanyAvatar.setImageDrawable(drawable)

        holder.tvCardCompanyName.text = company.name
        holder.tvCardCompanyCvsSegment.text = context.resources.getStringArray(R.array.companyCVS_array)[company.cvsSegment]
        holder.tvCardCompanyStatus.text = context.resources.getStringArray(R.array.status_array)[company.status]

        holder.constraintLayoutCompanyMain.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime >= 2000) {
                lastClickTime = SystemClock.elapsedRealtime()
                listenerCompanies.recyclerViewCompaniesOnClick(
                        holder.ivCompanyAvatar,
                        companyList[position],
                        isForSelect
                )
            }
        }

        if (isForSelect) {
            holder.btnCardCompanySelectCompany.visibility = View.VISIBLE
            holder.btnCardCompanySelectCompany.isEnabled = true

            holder.btnCardCompanySelectCompany.setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastClickTime >= 2000) {
                    lastClickTime = SystemClock.elapsedRealtime()
                    ContactEditFragment.chosenCompany = company
                    context.onBackPressed()
                }
            }
        } else {
            holder.btnCardCompanySelectCompany.visibility = View.GONE
            holder.btnCardCompanySelectCompany.isEnabled = false
        }
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    fun filter(filteredList: ArrayList<Company>) {
        companyList = filteredList
        notifyDataSetChanged()
    }

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardCompanyName: TextView = itemView.tvCardCompanyName
        val tvCardCompanyCvsSegment: TextView = itemView.tvCardCompanyCvsSegment
        val tvCardCompanyStatus: TextView = itemView.tvCardCompanyStatus

        val ivCompanyAvatar: ImageView = itemView.ivCompanyAvatar
        val btnCardCompanySelectCompany: Button = itemView.btnCardCompanySelectCompany
        val constraintLayoutCompanyMain: ConstraintLayout = itemView.constraintLayoutCompanyMain
    }
}

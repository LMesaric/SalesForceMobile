package hr.atoscvc.salesforcemobile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth


class ContactsFragment : Fragment() {

    companion object {
        const val requestCodeRefresh = 3
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewContacts)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mAuth = FirebaseAuth.getInstance()

        val contactList = ArrayList<Contact>()

        contactList.add(Contact(
                "akjssgddad",
                0,
                1,
                "Ana",
                "Peric",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "091251244",
                "email@email.com",
                1,
                "asdjbajhbdsnadjhas asda sda safsd fasdgasdg asdga sdgasdg asdg sdgasd ggasdg asdg asdgas dgasdg sadg sdg asdgg asdg sadg"
        ))
        contactList.add(Contact(
                "tffcvbh",
                1,
                0,
                "Matej",
                "Bubic",
                Company(
                        "knksgnf",
                        1,
                        "45678934",
                        "Siemens",
                        "siemens.hr",
                        1,
                        "Very great company",
                        "01484567",
                        0,
                        4,
                        "100000"
                ),
                "087654567899",
                "email@gmail.com",
                0,
                "alksnbdabnsd"
        ))
        contactList.add(Contact(
                "klejwf",
                1,
                3,
                "Tea",
                "Bubic",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "56456789867",
                "yahoo@gmail.com",
                1,
                "masjdban asdaisd"
        ))
        contactList.add(Contact(
                "akjssgddad",
                0,
                1,
                "Ana",
                "Peric",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "091251244",
                "email@email.com",
                1,
                "asdjbajhbdsnadjhas asda sda safsd fasdgasdg asdga sdgasdg asdg sdgasd ggasdg asdg asdgas dgasdg sadg sdg asdgg asdg sadg"
        ))
        contactList.add(Contact(
                "tffcvbh",
                1,
                0,
                "Matej",
                "Bubic",
                Company(
                        "knksgnf",
                        1,
                        "45678934",
                        "Siemens",
                        "siemens.hr",
                        1,
                        "Very great company",
                        "01484567",
                        0,
                        4,
                        "100000"
                ),
                "087654567899",
                "email@gmail.com",
                0,
                "alksnbdabnsd"
        ))
        contactList.add(Contact(
                "klejwf",
                1,
                3,
                "Tea",
                "Bubic",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "56456789867",
                "yahoo@gmail.com",
                1,
                "masjdban asdaisd"
        ))
        contactList.add(Contact(
                "akjssgddad",
                0,
                1,
                "Ana",
                "Peric",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "091251244",
                "email@email.com",
                1,
                "asdjbajhbdsnadjhas asda sda safsd fasdgasdg asdga sdgasdg asdg sdgasd ggasdg asdg asdgas dgasdg sadg sdg asdgg asdg sadg"
        ))
        contactList.add(Contact(
                "tffcvbh",
                1,
                0,
                "Matej",
                "Bubic",
                Company(
                        "knksgnf",
                        1,
                        "45678934",
                        "Siemens",
                        "siemens.hr",
                        1,
                        "Very great company",
                        "01484567",
                        0,
                        4,
                        "100000"
                ),
                "087654567899",
                "email@gmail.com",
                0,
                "alksnbdabnsd"
        ))
        contactList.add(Contact(
                "klejwf",
                1,
                3,
                "Tea",
                "Bubic",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "56456789867",
                "yahoo@gmail.com",
                1,
                "masjdban asdaisd"
        ))
        contactList.add(Contact(
                "akjssgddad",
                0,
                1,
                "Ana",
                "Peric",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "091251244",
                "email@email.com",
                1,
                "asdjbajhbdsnadjhas asda sda safsd fasdgasdg asdga sdgasdg asdg sdgasd ggasdg asdg asdgas dgasdg sadg sdg asdgg asdg sadg"
        ))
        contactList.add(Contact(
                "tffcvbh",
                1,
                0,
                "Matej",
                "Bubic",
                Company(
                        "knksgnf",
                        1,
                        "45678934",
                        "Siemens",
                        "siemens.hr",
                        1,
                        "Very great company",
                        "01484567",
                        0,
                        4,
                        "100000"
                ),
                "087654567899",
                "email@gmail.com",
                0,
                "alksnbdabnsd"
        ))
        contactList.add(Contact(
                "klejwf",
                1,
                3,
                "Tea",
                "Bubic",
                Company(
                        "asdbnasd",
                        0,
                        "976754689",
                        "Atos CVC",
                        "atos.net",
                        0,
                        "Very good company",
                        "012475479",
                        0,
                        2,
                        "150000"
                ),
                "56456789867",
                "yahoo@gmail.com",
                1,
                "masjdban asdaisd"
        ))
        val adapter = activity?.applicationContext?.let { ContactAdapter(contactList, it, false) }
        recyclerView.adapter = adapter

        /*val adapter = ContactAdapter(
                contactList,
                this,
                intent.getBooleanExtra(
                        getString(R.string.EXTRA_CONTACT_IS_LIST_FOR_SELECT),
                        false
                )
        )*/

        return view
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ContactsFragment.requestCodeRefresh) {
            if (resultCode == RESULT_OK) {
                recyclerViewContacts.adapter?.notifyDataSetChanged()
            }
        }
    }*/

}

package jp.co.casareal.fragmentresultapi_sample


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class MyDialogFragment : DialogFragment() {

    companion object{
        const val FRAGMENT_DIALOG_KEY = "fragment_dialog_key"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

       val dialog =  AlertDialog.Builder(requireContext()).apply {
            setMessage("FragmentDialogのメッセージですよ。")
            setTitle("FragmentDialogのタイトルですよ")
            setView(R.layout.fragment_my_dialog)
            setPositiveButton("OK" ){
                dialog, which ->
                // 結果を設定
                setFragmentResult(FRAGMENT_DIALOG_KEY, bundleOf("name" to "casareal"))
            }
        }.create()

        return dialog
    }
}

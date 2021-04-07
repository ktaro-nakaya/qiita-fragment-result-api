package jp.co.casareal.fragmentresultapi_sample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navGraphViewModels

class FirstFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // SecondFragment画面に遷移する
        requireActivity().findViewById<Button>(R.id.btnFirst).setOnClickListener {
            navController.navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment())
        }

        // DialogFragmentを表示する
        requireView().findViewById<Button>(R.id.btnFragmentDialog).setOnClickListener {
            MyDialogFragment().show(requireActivity().supportFragmentManager, null)
        }

    }

    override fun onResume() {
        super.onResume()

        // ViewModelで結果を受け取る方法
        val myViewModel: MyViewModel by navGraphViewModels<MyViewModel>(R.id.my_nav)
        myViewModel.stateHandle.get<String>("message")?.let {
            Toast.makeText(requireContext(), "ViewModel:${it}", Toast.LENGTH_SHORT).show()
        }

        // SecondFragment画面の結果を受け取る
        setFragmentResultListener(SecondFragment.MY_RESULT_KEY) { requestKey, bundle ->

            Log.i("TEST", bundle.getString("message") ?: "メッセージの取得に失敗")

        }

        // DialogFragmentの結果を受け取る
        requireActivity().supportFragmentManager.setFragmentResultListener(MyDialogFragment.FRAGMENT_DIALOG_KEY, this) { requestKey, bundle ->
            Toast.makeText(requireContext(), "Dialog Fragment Result:${bundle.getString("name")}", Toast.LENGTH_SHORT).show()
        }


    }
}

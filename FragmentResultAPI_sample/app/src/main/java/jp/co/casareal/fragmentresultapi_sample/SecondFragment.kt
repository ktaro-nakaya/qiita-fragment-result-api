package jp.co.casareal.fragmentresultapi_sample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navGraphViewModels

class SecondFragment : Fragment() {

    companion object{
        const val MY_RESULT_KEY = "RESULT_KEY"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        val navController = navHostFragment.navController

        requireActivity().findViewById<Button>(R.id.btnSecond).setOnClickListener {

            // ViewModelで結果を渡す
            val myViewModel:MyViewModel by navGraphViewModels<MyViewModel>(R.id.my_nav)
            myViewModel.stateHandle.set("message","vmに保存してみた")

            // 結果を設定
            setFragmentResult(MY_RESULT_KEY, bundleOf("message" to "Fragment Result APIで結果を渡す"))

            // 一つ前の画面に戻る
            navController.popBackStack()
        }
    }

}

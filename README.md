# はじめに
NavigationComponentのリリースにより、よりFragmentやFragmentDialogで結果を受け取る事が重要になってきました。
更に合わせる様に、Acitivity Result APIがリリースされています。
また、データの受け渡しはViewModelでも可能です。今回のサンプルではNavigation Componentを利用しているので、Navigation Component間でのViewModelを利用したデータの受け渡しを覚える必要があります。
この記事ではActivity Result APIとViewModelで結果を受け取る方法について説明します。

# 動作環境
この記事の動作環境は以下のとおりです。

Android Studio：4.1.2
Kotln：1.4.31
Open JDK:1.8
compileSdkVersion：30
targetSdkVersion：30
minSdkVersion：23
Navigation Component:2.3.2
activity-ktx:1.2.2
fragment-ktx:1.3.2

# 目標
以下を目標とします。

* Fragmentで結果を受け取る（Activity Result API）
* Fragmentで結果を受け取る（ViewModel）
* FragmentDialogで結果を受け取る

# 完成イメージ
<img src="https://github.com/ktaro-nakaya/qiita-fragment-result-api/blob/main/images/image.gif" width="50%">

# サンプルコード
サンプルコードは[github](https://github.com/ktaro-nakaya/qiita-fragment-result-api)からダウンロードできます。

# 事前準備
Activity Result APIを利用するので当然ですが、build.gradle（app）には以下の依存を追記しています。

```groovy
dependencies {
    implementation "androidx.activity:activity-ktx:1.2.2"
    implementation "androidx.fragment:fragment-ktx:1.3.2"
}
```

# Activity Result APIで結果を受け取る
## 実装イメージ
基本的な実装方法としては **キー** を利用して、画面遷移から返ってきた際のコールバックを設定します。
Activityの場合との違いとしては、Contractsなどは無いことです。

![01_activity_result_api.png](https://github.com/ktaro-nakaya/qiita-fragment-result-api/blob/main/images/Activity%20Result%20API%EF%BC%88Fragment%E7%B7%A8%EF%BC%89/01_activity_result_api.png)


## Navigation Component間で結果を受け取る

### 実装例
#### コールバックの設定
今回 **キー** は遷移先の画面に定数として定義しました。

```kotlin:SecondFragment.kt
class SecondFragment : Fragment() {
    companion object{
        const val MY_RESULT_KEY = "RESULT_KEY"
    }
}
```

コールバックは下記の通り、 **setFragmentResultListener関数** 関数で設定します。

```kotlin:FirstFragment.kt
// SecondFragment画面の結果を受け取る
setFragmentResultListener(SecondFragment.MY_RESULT_KEY) { requestKey, bundle ->

    Log.i("TEST", bundle.getString("message") ?: "メッセージの取得に失敗")

}
```

setFragmentResultListener関数のシグネチャーは下記の通りです。

|修飾子|メソッド名|戻り値|引数|
|:---|:---|:---|:----------|
|public final|  setFragmentResultListener| void | 第一引数：String requestKey<br> 第二引数：LifecycleOwner lifecycleOwner<br>第三引数：FragmentResultListener listener|

第一引数で、どの **キー** で結果をセットされたときに動作するかを設定します。
第二引数で、ライフサイクルオーナーを設定することにより、予期しないタイミングで動作することを防げます。
第三引数で、実際の処理を記述します。

FragmentResultListenerは下記のメソッドを実装する必要があります。

|修飾子|メソッド名|戻り値|引数|
|:---|:---|:---|:----------|
|public abstract|  onFragmentResult | void | 第一引数：String requestKey<br> 第二引数：Bundle result|

第一引数には、結果が設定されたときの **キー** が格納されています。setFragmentResultListenerの第一引数と一致します。
第二引数には、結果が設定されたときの受け渡したいデータを格納したBundle型のオブジェクトが格納されています。今回はラムダ式で実装しています。

#### 結果の設定
実際に結果を返す処理の記述方法です。
実際のコードは下記の通りです。

```kotlin
// 結果を設定
setFragmentResult(MY_RESULT_KEY, bundleOf("message" to "Fragment Result APIで結果を渡す"))
```

 **setFragmentResult** メソッドで結果を設定していきます。
シグネチャーは以下のとおりです。

|修飾子|メソッド名|戻り値|引数|
|:---|:---|:---|:----------|
|public final |  setFragmentResult | void | 第一引数：String requestKey<br> 第二引数：Bundle result|

第一引数は、動作させるコールバックと一致する **キー** を設定します。
第二引数は、遷移元に返す値をBunle型で渡します。

## DialogFragmentから結果を受け取る
DialogFragmentもFragmentだから、同じ様に実装すれば動くでしょ！！と勘違いしやすくハマるポイントです。
実際に渡しもハマりました。

### 実装例
実装例を見ていきましょう。

#### コールバックの設定
まずは実装例を確認します。

```kotlin:FirstFragment.kt
// DialogFragmentの結果を受け取る
requireActivity().supportFragmentManager.setFragmentResultListener(MyDialogFragment.FRAGMENT_DIALOG_KEY, this) { requestKey, bundle ->
    Toast.makeText(requireContext(), "Dialog Fragment Result:${bundle.getString("name")}", Toast.LENGTH_SHORT).show()
}
```

注意すべき点はFragmentではなく、supportFragmentManagerに設定しています。
なぜ？とお思いになったでしょう。
答えはダイアログを表示するところにヒントがあります。

```kotlin:FirstFragment.kt
MyDialogFragment().show(requireActivity().supportFragmentManager, null)
```
ダイアログ表示するときって、NavigationComponentでもなく、普通に **supportFragmentManager** で表示しているんですね。
なので、コールバックの設定も **supportFragmentManager** に設定しなきゃです。

#### 結果の設定
結果の設定は、これまで説明した方法と同じです。

```kotlin:MyDialogFragment.kt
setFragmentResult(FRAGMENT_DIALOG_KEY, bundleOf("name" to "casareal"))
```


# ViewModelでデータを共有
ここまではActivity Result APIで実装する例を説明してきました。
結局の所、データをFragment間で共有できればよいという考えだと、ViewModelを使っても良いわけですね。
しかし、ViewModelだとライフサイクルが長いので、うっかりミスが発生しちゃいそうです。
そこは、Androidで対応されているようなので、使ってみましょう。

## 実装イメージ
実装イメージは下図のとおりですが、ポイントは特定のFragment間でしか動作しないViewModelを生成できるようです。
![02_viewmodel.png](https://github.com/ktaro-nakaya/qiita-fragment-result-api/blob/main/images/Activity%20Result%20API%EF%BC%88Fragment%E7%B7%A8%EF%BC%89/02_viewmodel.png)

## ViewModelの取得
ViewModelを特定のFragment間のみで利用できるようにする方法の秘密はViewModelの取得方法にあります。
実際のコードを確認してみます。

```kotlin:FirstFragment.kt
// ViewModelで結果を受け取る方法
val myViewModel: MyViewModel by navGraphViewModels<MyViewModel>(R.id.my_nav)
```

navGraphViewModels関数でViewModelを取得しています。
[公式サイト](https://developer.android.com/guide/fragments/communicate?hl=ja)にも説明がありますが、backstackをライフサイクルとして扱うため、Fragmentの親子関でライフサイクルを限定出来きます。

そのため、引数にnavGraphIdを指定する必要があります。（/res/navigationのファイルのIDです。）

## データの受け渡しのコード例
実際のデータの受け渡しのコードを確認してみましょう。
ViewModelを取得する関数が変わるだけで、ViewModelの基本的な利用方法は変わりません。

### ViewModel
今回はこのようなViewModelを定義しました。

```kotlin:MyViewModel.kt
class MyViewModel(val stateHandle: SavedStateHandle): ViewModel()
```

### 子Fragmentで値を設定している例

```kotlin:SecondFragment.kt
// ViewModelで結果を渡す
val myViewModel:MyViewModel by navGraphViewModels<MyViewModel>(R.id.my_nav)
myViewModel.stateHandle.set("message","vmに保存してみた")
```

### 親Fragmentで値を取得している例

```kotlin:FirstFragment.kt
// ViewModelで結果を受け取る方法
val myViewModel: MyViewModel by navGraphViewModels<MyViewModel>(R.id.my_nav)
myViewModel.stateHandle.get<String>("message")?.let {
    Toast.makeText(requireContext(), "ViewModel:${it}", Toast.LENGTH_SHORT).show()
}
```

# まとめ
Navigation Componentがリリースされてから、Fragment間のデータの受け渡し方法がかなり簡潔になってきました。
一昔前だったら煩雑な手順を踏んでましたが、コールバックで実装できるのは便利ですね。
ぜひ活用していきたいものです。



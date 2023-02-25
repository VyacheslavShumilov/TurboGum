package com.vshum.turbogum.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.transition.Slide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.vshum.turbogum.App
import com.vshum.turbogum.Constants
import com.vshum.turbogum.databinding.FragmentRegistrationBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen


class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var appNavigator: AppNavigator
    private lateinit var db: FirebaseFirestore

    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences(
            Constants.PASSWORD,
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val enterSlide = Slide()
        enterSlide.slideEdge = Gravity.END
        enterSlide.duration = 300
        enterSlide.interpolator = DecelerateInterpolator()
        enterTransition = enterSlide

        val exitSlide = Slide()
        exitSlide.slideEdge = Gravity.START
        exitSlide.duration = 300
        exitSlide.interpolator = DecelerateInterpolator()
        exitTransition = exitSlide

        // Получить ссылку на базу данных Firestore из приложения
        Firebase.initialize(requireActivity())
        db = Firebase.firestore
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator = (context.applicationContext as App).servicesLocator.providerNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            /***
             * Этот код выполняет следующие действия:
             * 1.Получает экземпляр SharedPreferences через вызов getSharedPreferences() метода.
             * 2.Получает сохраненное значение пароля по ключу Constants.PASSWORD с помощью вызова метода getString().Если сохраненное значение отсутствует, то возвращается пустая строка "".
             * 3.Проверяет, что полученное значение не равно null, а также не является пустой строкой.
             * 4.Если полученное значение не является пустой строкой, то выполняется переход на экран списка объектов (WRAPPERS_LIST_SCREEN), используя appNavigator.navigateTo().
             * 5. Если сохраненный пароль отсутствует или является пустой строкой, то выполнение кода продолжается без перехода на другой экран.
             * Этот код используется для проверки наличия сохраненного пароля пользователя в SharedPreferences и перенаправления пользователя на экран списка объектов,если сохраненный пароль присутствует.
             * Если пароль не сохранен или является пустой строкой, пользователь остается на текущем экране.
             */
            val savedPassword = sharedPreferences.getString(Constants.PASSWORD, "")
            if (savedPassword != null) {
                if (savedPassword.isNotEmpty()) {
                    appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                    return
                }
            }

            loginButton.setOnClickListener {
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()
                Log.d(TAG, "Email: $email, Password: $password")

                db.collection("UsersData")
                    /***
                     * whereEqualTo() для поиска документов в коллекции UsersData  у которых значение поля "email" совпадает со значением переменной email
                     * Затем вызывается метод get(), который возвращает результат запроса в виде объекта Task<QuerySnapshot>
                     */
                    .whereEqualTo("email", email)
                    .get()
                    /***
                     * При успешном выполнении запроса, метод addOnSuccessListener() вызывается для обработки полученного результата.
                     * Если запрос выполнен успешно, то переменной result будет присвоено значение объекта QuerySnapshot,
                     * который содержит все найденные документы из коллекции UsersData, у которых значение поля "email" равно значению переменной email
                     */
                    .addOnSuccessListener { result ->
                        Log.d("SIZE OF DB", "Result size: ${result.size()}")
                        /***
                         * Далее код проверяет, есть ли результаты запроса (result.size() > 0). Если да, то находим первый документ, соответствующий запросу (val user = result.documents[0]),
                         */
                        if (result.size() > 0) {
                            val user = result.documents[0]
                            // и проверяем, совпадает ли значение поля "password" этого документа с введенным пользователем паролем.
                            if (user.getString("password") == password) {

                                /***
                                 * Пользователь успешно аутентифицирован. Сохраняем пароль в SharedPreferences
                                 * Метод edit() возвращает объект SharedPreferences.Editor, который используется для редактирования SharedPreferences.
                                 * Метод putString(key, value) используется для сохранения значения value под ключом key в SharedPreferences.
                                 * В данном случае, key - это значение константы Constants.PASSWORD, а value - это значение переменной password, которое мы получили от пользователя.
                                 * Таким образом, sharedPreferences.edit().putString(Constants.PASSWORD, password).apply() сохраняет пароль пользователя в SharedPreferences
                                 * под ключом Constants.PASSWORD. Мы можем получить это значение позже из любой части приложения, используя тот же самый ключ Constants.PASSWORD.
                                 */
                                sharedPreferences.edit().putString(Constants.PASSWORD, password)
                                    .apply()

                                // Переходим на экран WRAPPERS_LIST_SCREEN
                                appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
                            } else {
                                // Неправильный пароль
                                Toast.makeText(context, "Неправильный email или пароль", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Если запрос возвращает пустой результат (т.е. result.size() == 0), значит, пользователь с таким email не найден в базе данны
                            Toast.makeText(context, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                        }
                    }
                    /***
                     * Если выполнение запроса завершается неудачей (например, из-за ошибки сети), то вызывается метод addOnFailureListener(),
                     * в котором можно обработать ошибку и вывести сообщение об ошибке.
                     */
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Error getting documents: ", exception)
                        Toast.makeText(context, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                    }
            }


            helpButton.setOnClickListener {
                appNavigator.navigateTo(Screen.HELP_SCREEN)
            }

            policyTxt.setOnClickListener {
                val uri: Uri = Uri.parse(Constants.POLICY)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }
}
package com.vshum.turbogum.ui.liners_lists

import androidx.lifecycle.ViewModel
import com.vshum.turbogum.model.Liner

/***
 * LinersListViewModel нужна для хранения списков вкладышей по сериям.
 * Также она позволяет только один раз выполнить запрос в сеть и получить ВЕСЬ список вкладышей, далее сортировка проводится с данными сохраненными локально
 * В предыдущей реализации каждый раз, когда переходишь из списка на вкладыш или из оберток на список выполнялся запрос в сеть
 */

class LinersListViewModel : ViewModel() {
    var linersListLocal = arrayListOf<Liner>()

    var series1List = arrayListOf<Liner>()
    var series2List = arrayListOf<Liner>()
    var series3List = arrayListOf<Liner>()
    var series4List = arrayListOf<Liner>()
    var series5List = arrayListOf<Liner>()
    var super1List = arrayListOf<Liner>()
    var super2List = arrayListOf<Liner>()
    var super3List = arrayListOf<Liner>()
    var sport1List = arrayListOf<Liner>()
    var sport2List = arrayListOf<Liner>()
    var classic1List = arrayListOf<Liner>()
    var classic2List = arrayListOf<Liner>()
//        linersPowerList = ArrayList()
//        linersSport3List = ArrayList()
//        linersSport4List = ArrayList()
//        linersSport5List = ArrayList()

}

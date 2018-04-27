package br.com.arch.toolkit.recycler.adapter

import android.content.Context
import android.view.View

open class SimpleAdapter<MODEL, VIEW>(creator: (context: Context) -> VIEW) :
        BaseRecyclerAdapter<MODEL>({ context, _ -> creator.invoke(context) })
        where VIEW : View, VIEW : ViewBinder<MODEL>
package com.monkey.paintertest

import java.util.ArrayList

class Stack<T> (private val maxSize: Int) {
    private val stack: MutableList<T> = mutableListOf()

    fun pushNewThing(obj: T) {
        stack.add(obj)
        if(stack.size > maxSize) {
            stack.removeAt(0)
        }
    }

    fun popLastThing(): T {
        val returnHistory = stack[stack.size-1]
        stack.remove(returnHistory)
        return returnHistory
    }

    fun isEmpty(): Boolean {
        return stack.isEmpty()
    }

    fun clear() {
        stack.clear()
    }


}
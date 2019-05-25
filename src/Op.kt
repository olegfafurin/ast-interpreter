package ast_interpreter

/**
 * Created by imd on 25/05/2019
 */
abstract class Op() : Node(mutableListOf()) {
    abstract fun compute(args : List<Any>) : Any
}
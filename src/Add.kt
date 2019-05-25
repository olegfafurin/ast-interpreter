package ast_interpreter

/**
 * Created by imd on 25/05/2019
 */
class Add : Op() {
    override fun compute(args: List<Any>): Any {
        assert(args.all { it is Int })
        return args.sumBy { it as Int }
    }
}
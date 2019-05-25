package ast_interpreter

import java.lang.Exception

/**
 * Created by imd on 25/05/2019
 */

class ASTInterpreter(val program: Node) {

    private var jump: MutableMap<String, Node> = mutableMapOf()
    private var vars: MutableMap<String, Any> = mutableMapOf()

    private fun setLabels(node : Node) {
        when (node) {
            is Program -> {
                for (child in node.children) {
                    if (child is BasicBlock) setLabels(child)
                }
            }
            is BasicBlock -> {
                val label = node.children.first() as Label
                jump[label.name] = node
            }
        }
    }

    private fun interprete(node: Node): Any {
        when (node) {
            is Program -> {
                for (child in node.children) {
                    if (child is Var) {
                        val varName = child.name
                        print("Set variable $varName: ")
                        vars[varName] = readLine()?.toInt() ?: 0
                    } else return interprete(child)
                }
            }
            is BasicBlock -> {
                for (child in node.children.subList(1, node.children.size)) {
                    interprete(child)
                }
            }
            is Assignment -> {
                val varName = interprete(node.children.first()) as String
                vars[varName] = interprete(node.children[1])
            }
            is Label -> {
                return node.name
            }
            is Jump -> {
                if (node.children.size == 1) {
                    val child = node.children.first()
                    if (child is Label) return interprete(jump[interprete(child)]!!)
                    if (child is Expr) return interprete(child)
                    else throw IllegalArgumentException("Unexpected kind of child: " + child.javaClass.name + " found at Jump")
                } else {
                    if (node.children.size == 3) {
                        val condition = node.children.first()
                        val thenJump = node.children[1]
                        val elseJump = node.children[2]
                        if (condition is Expr && thenJump is Label && elseJump is Label) {
                            return if (interprete(condition) as Boolean) interprete(jump[thenJump.name]!!)
                            else interprete(jump[elseJump.name]!!)
                        }
                    }
                    throw IllegalArgumentException("Unexpected kind of children")
                }
            }
            is Expr -> {
                val firstChild = node.children.first()
                if (firstChild is Constant) return firstChild.value
                if (firstChild is Op) {
                    return firstChild.compute(node.children.subList(1,node.children.size).map { interprete(it) })
                } else throw IllegalArgumentException("Unexpected kind of child: " + firstChild.javaClass.name + " found at Expr")
            }
            else -> throw IllegalArgumentException("Unexpected kind of Node while interpretation" + node.javaClass.name)
        }
        return Any()
    }

    fun process() {
        setLabels(program)
        val result = interprete(program)
        print(result)
    }
}
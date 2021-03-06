
open class Human(val name:String, val hasMana:Boolean){
    open fun attack(){
        println("$name use Fist Attack!")
    }
    fun manaCheck(){
        if (hasMana){
            println("$name has mana!")
        } else  println("$name has no mana!")
    }
}
class Mage (name : String, hasMana : Boolean): Human(name, hasMana){
    override fun attack(){
        println("$name use Fireball!")
    }
}
fun main(args: Array<String>) {
    val james = Human("James",false)
    james.manaCheck()
    james.attack()

    val ivy = Mage("Ivy", true)
    ivy.manaCheck()
    ivy.attack()
}
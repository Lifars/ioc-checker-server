//package com.lifars.ioc.server.utils
//
//import java.util.*
//
//class IocEntryTree<V>(
//    private val root: V,
//    private val children: V.() -> Iterable<V>
//) {
//    fun <R> traverseFromBottom(
//        vertexProcessor: (vertex: V, parent: V?) -> R
//    ): List<Pair<V, R>> {
//        val stack: Deque<V> = ArrayDeque()
//        val queue: Queue<V> = ArrayDeque()
//
//        val parents = mutableMapOf<V, V>()
//
//        queue.add(root);
//
//        while (queue.isNotEmpty()) {
//            val vertex = queue.remove()
//            stack.push(vertex)
//
//            vertex.children().forEach { child ->
//                parents[child] = vertex
//                queue.add(child)
//            }
//        }
//
//        val results = mutableListOf<Pair<V, R>>()
//        while (stack.isNotEmpty()) {
//            val vertex = stack.pop()
//            val parent = parents[vertex]
//            val value = vertexProcessor(vertex, parent)
//            results.add(vertex to value)
//        }
//        return results
//    }
//
//    fun <R> traverseFromTop(
//        vertexProcessor: (vertex: V, parent: V?) -> R
//    ): List<Pair<V, R>> {
//        val stack: Deque<V> = ArrayDeque()
//
//        val parents = mutableMapOf<V, V>()
//
//        stack.push(root);
//
//        while (stack.isNotEmpty()) {
//            val vertex = stack.pop()
//            vertex.children().forEach { child ->
//                parents[child] = vertex
//                stack.push(child)
//            }
//        }
//
//        val results = mutableListOf<Pair<V, R>>()
//        while (stack.isNotEmpty()) {
//            val vertex = stack.pop()
//            val parent = parents[vertex]
//            val value = vertexProcessor(vertex, parent)
//            results.add(vertex to value)
//        }
//        return results
//    }
//}
//

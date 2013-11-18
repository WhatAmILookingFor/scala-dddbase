package org.sisioh.dddbase.core.lifecycle.async

import org.sisioh.dddbase.core.lifecycle.{EntityIOContext, EntityReadableByPredicate, EntitiesChunk}
import org.sisioh.dddbase.core.model.{Entity, Identity}
import scala.concurrent.Future

/**
 * 非同期版[[org.sisioh.dddbase.core.lifecycle.EntityReadableByPredicate]]。
 *
 * @tparam ID 識別子の型
 * @tparam E エンティティの型
 */
trait AsyncEntityReadableByPredicate[CTX <: EntityIOContext[Future] ,ID <: Identity[_], E <: Entity[ID]]
  extends EntityReadableByPredicate[CTX, ID, E, Future] {
  this: AsyncEntityReader[CTX, ID, E] =>

  /**
   * @return Success:
   *         チャンク
   *         Failure:
   *         EntityNotFoundExceptionは、エンティティが見つからなかった場合
   *         RepositoryExceptionは、リポジトリにアクセスできなかった場合。
   */
  def filterByPredicate
  (predicate: E => Boolean, index: Option[Int] = None, maxEntities: Option[Int] = None)
  (implicit ctx: CTX): Future[EntitiesChunk[ID, E]]

}

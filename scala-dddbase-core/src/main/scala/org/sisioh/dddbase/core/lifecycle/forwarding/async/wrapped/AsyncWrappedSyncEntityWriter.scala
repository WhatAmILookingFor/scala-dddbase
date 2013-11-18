package org.sisioh.dddbase.core.lifecycle.forwarding.async.wrapped

import org.sisioh.dddbase.core.lifecycle.async.{AsyncResultWithEntity, AsyncEntityWriter}
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityWriter
import org.sisioh.dddbase.core.model.{Entity, Identity}
import scala.concurrent._
import org.sisioh.dddbase.core.lifecycle.EntityIOContext
import scala.util.Try

/**
 * [[org.sisioh.dddbase.core.lifecycle.sync.SyncEntityWriter]]を
 * [[org.sisioh.dddbase.core.lifecycle.async.AsyncEntityWriter]]として
 * ラップするためのデコレータ。
 *
 * @tparam ID 識別子の型
 * @tparam E エンティティの型
 */
trait AsyncWrappedSyncEntityWriter[CTX <: EntityIOContext[Future], ID <: Identity[_], E <: Entity[ID]]
  extends AsyncEntityWriter[CTX, ID, E] with AsyncWrappedSyncEntityIO {

  type Delegate <: SyncEntityWriter[EntityIOContext[Try], ID, E]

  /**
   * デリゲート。
   */
  protected val delegate: Delegate

  protected def createInstance(state: (Delegate#This, Option[E])): (This, Option[E])

  def store(entity: E)(implicit ctx: CTX): Future[AsyncResultWithEntity[This, CTX, ID, E]] = {
    val asyncCtx = getAsyncWrappedEntityIOContext(ctx)
    implicit val executor = asyncCtx.executor
    future {
      implicit val syncCtx = asyncCtx.syncEntityIOContext
      val resultWithEntity = delegate.store(entity).get
      val result = createInstance((resultWithEntity.result.asInstanceOf[Delegate#This], Some(resultWithEntity.entity)))
      AsyncResultWithEntity[This, CTX, ID, E](result._1.asInstanceOf[This], result._2.get)
    }
  }

  def deleteByIdentity(identity: ID)(implicit ctx: CTX): Future[AsyncResultWithEntity[This, CTX, ID, E]] = {
    val asyncCtx = getAsyncWrappedEntityIOContext(ctx)
    implicit val executor = asyncCtx.executor
    future {
      implicit val syncCtx = asyncCtx.syncEntityIOContext
      val resultWithEntity = delegate.deleteByIdentity(identity).get
      val result = createInstance((resultWithEntity.result.asInstanceOf[Delegate#This], Some(resultWithEntity.entity)))
      AsyncResultWithEntity[This, CTX, ID, E](result._1.asInstanceOf[This], result._2.get)
    }
  }

}

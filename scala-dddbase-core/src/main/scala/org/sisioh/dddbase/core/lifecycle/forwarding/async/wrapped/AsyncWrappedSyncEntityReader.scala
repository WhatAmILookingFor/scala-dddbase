package org.sisioh.dddbase.core.lifecycle.forwarding.async.wrapped

import org.sisioh.dddbase.core.lifecycle.async.AsyncEntityReader
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityReader
import org.sisioh.dddbase.core.model.{Entity, Identity}
import scala.concurrent._
import org.sisioh.dddbase.core.lifecycle.EntityIOContext
import scala.util.Try

/**
 * [[org.sisioh.dddbase.core.lifecycle.sync.SyncEntityReader]]を
 * [[org.sisioh.dddbase.core.lifecycle.async.AsyncEntityReader]]として
 * ラップするためのデコレータ。
 *
 * @tparam ID 識別子の型
 * @tparam E エンティティの型
 */
trait AsyncWrappedSyncEntityReader[CTX <: EntityIOContext[Future], ID <: Identity[_], E <: Entity[ID]]
  extends AsyncEntityReader[CTX, ID, E] with AsyncWrappedSyncEntityIO {

  type Delegate <: SyncEntityReader[EntityIOContext[Try], ID, E]

  protected val delegate: Delegate

  def resolve(identity: ID)(implicit ctx: CTX) = {
    val asyncCtx = getAsyncWrappedEntityIOContext(ctx)
    implicit val executor = asyncCtx.executor
    future {
      implicit val syncCtx = asyncCtx.syncEntityIOContext
      delegate.resolve(identity).get
    }
  }

  def containsByIdentity(identity: ID)(implicit ctx: CTX): Future[Boolean] = {
    val asyncCtx = getAsyncWrappedEntityIOContext(ctx)
    implicit val executor = asyncCtx.executor
    future {
      implicit val syncCtx = asyncCtx.syncEntityIOContext
       delegate.containsByIdentity(identity).get
    }
  }

  override def contains(entity: E)(implicit ctx: CTX): Future[Boolean] =
    containsByIdentity(entity.identity)
}

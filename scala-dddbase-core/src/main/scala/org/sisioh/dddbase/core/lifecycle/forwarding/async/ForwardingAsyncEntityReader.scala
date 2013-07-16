package org.sisioh.dddbase.core.lifecycle.forwarding.async

import org.sisioh.dddbase.core.model.{Entity, Identity}
import scala.concurrent.{Future, ExecutionContext}
import org.sisioh.dddbase.core.lifecycle.async.AsyncEntityReader

trait ForwardingAsyncEntityReader[ID <: Identity[_], T <: Entity[ID]] extends AsyncEntityReader[ID, T] {

  protected val delegateAsyncEntityReader: AsyncEntityReader[ID, T]

  def resolve(identity: ID) =
    delegateAsyncEntityReader.resolve(identity)

  def contains(identity: ID): Future[Boolean] =
    delegateAsyncEntityReader.contains(identity)

}
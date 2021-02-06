package rsp.admin.crud.services;

import rsp.admin.crud.services.Create;
import rsp.admin.crud.services.Delete;
import rsp.admin.crud.services.GetList;
import rsp.admin.crud.services.GetOne;
import rsp.admin.crud.services.Update;

public interface EntityService<K, T> extends GetOne<K, T>, GetList<K, T>, Create<K, T>, Delete<K, T>, Update<K, T> {
}

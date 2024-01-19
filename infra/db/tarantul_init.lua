box.cfg{}
box.schema.space.create('dialogs', {if_not_exists=true})
box.space.dialogs:format({
    { name = 'id', type = 'unsigned', is_nullable = false },
    { name = 'fromUserId', type = 'unsigned', is_nullable = false },
    { name = 'toUserId', type = 'unsigned', is_nullable = false },
    { name = 'text', type = 'string', is_nullable = false },
    { name = 'createdAt', type = 'unsigned', is_nullable = false }
})
box.schema.sequence.create('dialogs_seq', {if_not_exists=true})
box.space.dialogs:create_index('primary', {if_not_exists=true, type='tree', unique=true, parts={'id'}, sequence='dialogs_seq'})
box.space.dialogs:create_index('search_by_users', {if_not_exists=true, type='tree', unique=false, parts={'fromUserId', 'toUserId'}})

function find_dialod_messages(fromUserId, toUserId)
    return box.space.dialogs.index.search_by_users:select({ fromUserId, toUserId})
end

function add_dialod_messages(fromUserId, toUserId, text, createdAt)
    return box.space.dialogs:insert({nil, fromUserId, toUserId, text, createdAt})
end

box.schema.user.create("test",{if_not_exists=true, password = "test"})
box.schema.user.grant('test', 'read,write,execute', 'universe', nil, {if_not_exists=true})

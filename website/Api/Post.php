<?php
require_once '../Controllers/Configs.php';

header('Content-Type: application/json');

$key = $_GET['key'] ?? '';
if ($key !== 'NguyenDucKien-ImS') {
    echo json_encode(['success' => false, 'message' => 'Invalid key.']);
    exit;
}

function handleDatabase($query, $params = [])
{
    global $Connect;
    try {
        $stmt = $Connect->prepare($query);
        foreach ($params as $key => $value) {
            $stmt->bindValue($key, $value);
        }
        return $stmt->execute();
    } catch (PDOException $e) {
        return false;
    }
}

// Xử lý POST - Thêm bài viết mới
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $postTitle = trim($_POST['postTitle'] ?? '');
    $postDescription = trim($_POST['postDescription'] ?? '');
    $postCategory = isset($_POST['postCategory']) ? intval($_POST['postCategory']) : 0;

    if (empty($postTitle) || empty($postDescription)) {
        echo json_encode(['success' => false, 'message' => 'Vui lòng điền đầy đủ thông tin.']);
        exit;
    }

    $encodedTitle = htmlentities($postTitle, ENT_QUOTES, 'UTF-8');
    $encodedDescription = htmlentities($postDescription, ENT_QUOTES, 'UTF-8');
    $jsonComments = json_encode([]);

    $query = "INSERT INTO posts (title, description, category, comments, created_at) 
              VALUES (:title, :description, :category, :comments, NOW())";
    $params = [
        ':title'       => $encodedTitle,
        ':description' => $encodedDescription,
        ':category'    => $postCategory,
        ':comments'    => $jsonComments
    ];

    if (handleDatabase($query, $params)) {
        echo json_encode(['success' => true, 'message' => 'Bài viết đã được đăng thành công.']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Không thể đăng bài viết.']);
    }
    exit;
}

// Xử lý PUT - Cập nhật bài viết
if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    parse_str(file_get_contents("php://input"), $_PUT);
    $postTitle = trim($_PUT['postTitle'] ?? '');
    $postDescription = trim($_PUT['postDescription'] ?? '');
    $postCategory = isset($_PUT['postCategory']) ? intval($_PUT['postCategory']) : 0;
    $id = intval($_GET['id'] ?? 0);

    if ($id === 0) {
        echo json_encode(['success' => false, 'message' => 'ID không hợp lệ.']);
        exit;
    }

    $encodedTitle = htmlentities($postTitle, ENT_QUOTES, 'UTF-8');
    $encodedDescription = htmlentities($postDescription, ENT_QUOTES, 'UTF-8');

    $query = "UPDATE posts SET title = :title, description = :description, category = :category WHERE id = :id";
    $params = [
        ':id'          => $id,
        ':title'       => $encodedTitle,
        ':description' => $encodedDescription,
        ':category'    => $postCategory,
    ];

    if (handleDatabase($query, $params)) {
        echo json_encode(['success' => true, 'message' => 'Bài viết đã được cập nhật thành công.']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Không thể cập nhật bài viết.']);
    }
    exit;
}

// Xử lý GET - Lấy bài viết theo ID hoặc danh sách bài viết
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (isset($_GET['id'])) {
        $id = intval($_GET['id']);
        $stmt = $Connect->prepare("SELECT id, title, description, category FROM posts WHERE id = :id");
        $stmt->bindParam(':id', $id, PDO::PARAM_INT);
        $stmt->execute();
        $post = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($post) {
            $post['title'] = html_entity_decode($post['title']);
            $post['description'] = html_entity_decode($post['description']);
            echo json_encode(['success' => true, 'post' => $post]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Không tìm thấy bài viết.']);
        }
    } else {
        $stmt = $Connect->query("SELECT id, title, description, category FROM posts ORDER BY created_at DESC");
        $posts = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $decodedPosts = [];

        foreach ($posts as $post) {
            $post['title'] = html_entity_decode($post['title']);
            $post['description'] = html_entity_decode($post['description']);
            $decodedPosts[] = $post;
        }
        echo json_encode(['success' => true, 'posts' => $decodedPosts]);
    }
    exit;
}

// Xử lý DELETE - Xóa bài viết
if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $id = intval($_GET['id'] ?? 0);
    if ($id === 0) {
        echo json_encode(['success' => false, 'message' => 'ID không hợp lệ.']);
        exit;
    }

    try {
        $stmt = $Connect->prepare("DELETE FROM posts WHERE id = :id");
        $stmt->bindParam(':id', $id);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            echo json_encode(['success' => true, 'message' => 'Bài viết đã được xóa.']);
        } else {
            echo json_encode(['success' => false, 'message' => 'Không tìm thấy bài viết để xóa.']);
        }
    } catch (PDOException $e) {
        echo json_encode(['success' => false, 'message' => 'Lỗi: ' . $e->getMessage()]);
    }
    exit;
}

// Phương thức không hợp lệ
http_response_code(405);
echo json_encode(['success' => false, 'message' => 'Phương thức không được hỗ trợ.']);
